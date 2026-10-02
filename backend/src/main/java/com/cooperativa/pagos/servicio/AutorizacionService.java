package com.cooperativa.pagos.servicio;

import com.cooperativa.pagos.dominio.Cuenta;
import com.cooperativa.pagos.dominio.EstadoPago;
import com.cooperativa.pagos.dominio.Pago;
import com.cooperativa.pagos.integracion.BancoExterno;
import com.cooperativa.pagos.integracion.BancoNoDisponibleException;
import com.cooperativa.pagos.integracion.NotificacionPublisher;
import com.cooperativa.pagos.integracion.ResultadoBanco;
import com.cooperativa.pagos.repositorio.CuentaRepository;
import com.cooperativa.pagos.repositorio.PagoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutorizacionService {

    private final PagoRepository pagos;
    private final CuentaRepository cuentas;
    private final AuditoriaService auditoria;
    private final BancoExterno banco;
    private final NotificacionPublisher notificaciones;

    public AutorizacionService(PagoRepository pagos, CuentaRepository cuentas,
                               AuditoriaService auditoria, BancoExterno banco,
                               NotificacionPublisher notificaciones) {
        this.pagos = pagos;
        this.cuentas = cuentas;
        this.auditoria = auditoria;
        this.banco = banco;
        this.notificaciones = notificaciones;
    }

    public Pago autorizar(String clave, String numeroCuenta, BigDecimal monto) {
        Optional<Pago> previo = pagos.findByClaveIdempotencia(clave);
        if (previo.isPresent()) {
            return reintento(previo.get(), numeroCuenta, monto);
        }

        Pago pago = new Pago(clave, numeroCuenta, monto);
        try {
            pago = pagos.saveAndFlush(pago);
        } catch (DataIntegrityViolationException e) {
            // Otro request con la misma clave entro al mismo tiempo
            Pago existente = pagos.findByClaveIdempotencia(clave).orElseThrow();
            return reintento(existente, numeroCuenta, monto);
        }
        auditoria.registrar(pago.getId(), "SOLICITUD_RECIBIDA",
                "cuenta=" + enmascarar(numeroCuenta) + " monto=" + monto + " clave=" + clave);

        Optional<Cuenta> buscada = cuentas.findById(numeroCuenta);
        if (buscada.isEmpty() || !buscada.get().isActiva()) {
            return cerrar(pago, EstadoPago.RECHAZADO, "CUENTA_INVALIDA", null);
        }
        Cuenta cuenta = buscada.get();

        if (monto.compareTo(cuenta.getLimitePorPago()) > 0) {
            return cerrar(pago, EstadoPago.RECHAZADO, "LIMITE_POR_PAGO", null);
        }

        Instant inicioDia = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        BigDecimal acumulado = pagos.sumaDesde(numeroCuenta,
                List.of(EstadoPago.AUTORIZADO, EstadoPago.EN_REVISION), inicioDia);
        if (acumulado == null) {
            acumulado = BigDecimal.ZERO;
        }
        if (acumulado.add(monto).compareTo(cuenta.getLimiteDiario()) > 0) {
            return cerrar(pago, EstadoPago.RECHAZADO, "LIMITE_DIARIO", null);
        }

        try {
            ResultadoBanco resultado = banco.autorizar(pago.getId(), numeroCuenta, monto);
            if (resultado.aprobado()) {
                return cerrar(pago, EstadoPago.AUTORIZADO, "OK", resultado.referencia());
            }
            return cerrar(pago, EstadoPago.RECHAZADO, "RECHAZADO_POR_BANCO", resultado.referencia());
        } catch (BancoNoDisponibleException e) {
            return cerrar(pago, EstadoPago.EN_REVISION, "BANCO_NO_RESPONDE", null);
        }
    }

    public Pago buscar(UUID id) {
        return pagos.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }

    private Pago reintento(Pago existente, String cuenta, BigDecimal monto) {
        boolean mismaSolicitud = existente.getCuenta().equals(cuenta)
                && existente.getMonto().compareTo(monto) == 0;
        if (!mismaSolicitud) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La clave de idempotencia ya se uso con otra solicitud");
        }
        auditoria.registrar(existente.getId(), "REINTENTO_IDEMPOTENTE",
                "Se devolvio el resultado ya guardado, no se cobro de nuevo");
        return existente;
    }

    private Pago cerrar(Pago pago, EstadoPago estado, String motivo, String referencia) {
        pago.resolver(estado, motivo, referencia);
        Pago guardado = pagos.save(pago);
        auditoria.registrar(guardado.getId(), "RESULTADO_" + estado,
                "motivo=" + motivo + " referenciaBanco=" + referencia);
        notificaciones.publicar(guardado);
        return guardado;
    }

    private static String enmascarar(String cuenta) {
        if (cuenta == null || cuenta.length() <= 4) {
            return "****";
        }
        return "****" + cuenta.substring(cuenta.length() - 4);
    }
}
