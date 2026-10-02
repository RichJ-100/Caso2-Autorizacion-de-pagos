package com.cooperativa.pagos.servicio;

import com.cooperativa.pagos.dominio.EstadoPago;
import com.cooperativa.pagos.dominio.Pago;
import com.cooperativa.pagos.integracion.BancoExterno;
import com.cooperativa.pagos.integracion.BancoNoDisponibleException;
import com.cooperativa.pagos.integracion.NotificacionPublisher;
import com.cooperativa.pagos.integracion.ResultadoBanco;
import com.cooperativa.pagos.repositorio.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ConciliacionService {

    private static final Logger log = LoggerFactory.getLogger(ConciliacionService.class);

    private final PagoRepository pagos;
    private final BancoExterno banco;
    private final AuditoriaService auditoria;
    private final NotificacionPublisher notificaciones;

    public ConciliacionService(PagoRepository pagos, BancoExterno banco,
                               AuditoriaService auditoria, NotificacionPublisher notificaciones) {
        this.pagos = pagos;
        this.banco = banco;
        this.auditoria = auditoria;
        this.notificaciones = notificaciones;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    public void ejecutarProgramado() {
        int resueltos = conciliar();
        if (resueltos > 0) {
            log.info("Conciliacion automatica: {} pagos resueltos", resueltos);
        }
    }

    public int conciliar() {
        int resueltos = 0;
        for (Pago pago : pagos.findByEstado(EstadoPago.EN_REVISION)) {
            try {
                ResultadoBanco resultado = banco.consultar(pago.getId());
                EstadoPago nuevo = resultado.aprobado() ? EstadoPago.AUTORIZADO : EstadoPago.RECHAZADO;
                pago.resolver(nuevo, "CONCILIADO", resultado.referencia());
                Pago guardado = pagos.save(pago);
                auditoria.registrar(guardado.getId(), "CONCILIACION", "estadoFinal=" + nuevo);
                notificaciones.publicar(guardado);
                resueltos++;
            } catch (BancoNoDisponibleException e) {
                log.warn("Banco aun no responde para el pago {}", pago.getId());
            }
        }
        return resueltos;
    }
}
