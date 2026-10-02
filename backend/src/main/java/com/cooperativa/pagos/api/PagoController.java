package com.cooperativa.pagos.api;

import com.cooperativa.pagos.dominio.Auditoria;
import com.cooperativa.pagos.dominio.Pago;
import com.cooperativa.pagos.servicio.AuditoriaService;
import com.cooperativa.pagos.servicio.AutorizacionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public PagoController(AutorizacionService autorizacion, AuditoriaService auditoria) {
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    @PostMapping
    public ResponseEntity<PagoRespuesta> crear(
            @RequestHeader("Idempotency-Key") String clave,
            @Valid @RequestBody SolicitudPago solicitud) {

        Pago pago = autorizacion.autorizar(clave, solicitud.cuenta(), solicitud.monto());

        HttpStatus estado = switch (pago.getEstado()) {
            case AUTORIZADO -> HttpStatus.OK;
            case RECHAZADO -> HttpStatus.UNPROCESSABLE_ENTITY;
            default -> HttpStatus.ACCEPTED;
        };
        return ResponseEntity.status(estado).body(PagoRespuesta.de(pago));
    }

    @GetMapping("/{id}")
    public PagoRespuesta consultar(@PathVariable UUID id) {
        return PagoRespuesta.de(autorizacion.buscar(id));
    }

    @GetMapping("/{id}/auditoria")
    public List<Auditoria> auditoria(@PathVariable UUID id) {
        autorizacion.buscar(id);
        return auditoria.listar(id);
    }
}
