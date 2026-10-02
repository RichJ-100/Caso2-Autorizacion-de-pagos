package com.cooperativa.pagos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auditoria_pago")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID pagoId;

    @Column(nullable = false)
    private String evento;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false)
    private Instant creadoEn;

    protected Auditoria() {
    }

    public Auditoria(UUID pagoId, String evento, String detalle) {
        this.pagoId = pagoId;
        this.evento = evento;
        this.detalle = detalle;
        this.creadoEn = Instant.now();
    }

    public Long getId() { return id; }
    public UUID getPagoId() { return pagoId; }
    public String getEvento() { return evento; }
    public String getDetalle() { return detalle; }
    public Instant getCreadoEn() { return creadoEn; }
}
