package com.cooperativa.pagos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    private UUID id;

    @Column(name = "clave_idempotencia", nullable = false, unique = true)
    private String claveIdempotencia;

    @Column(nullable = false)
    private String cuenta;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    private String motivo;

    private String referenciaBanco;

    @Column(nullable = false)
    private Instant creadoEn;

    @Column(nullable = false)
    private Instant actualizadoEn;

    protected Pago() {
    }

    public Pago(String claveIdempotencia, String cuenta, BigDecimal monto) {
        this.id = UUID.randomUUID();
        this.claveIdempotencia = claveIdempotencia;
        this.cuenta = cuenta;
        this.monto = monto;
        this.estado = EstadoPago.PENDIENTE;
        this.creadoEn = Instant.now();
        this.actualizadoEn = this.creadoEn;
    }

    public void resolver(EstadoPago nuevoEstado, String motivo, String referenciaBanco) {
        this.estado = nuevoEstado;
        this.motivo = motivo;
        this.referenciaBanco = referenciaBanco;
        this.actualizadoEn = Instant.now();
    }

    public UUID getId() { return id; }
    public String getClaveIdempotencia() { return claveIdempotencia; }
    public String getCuenta() { return cuenta; }
    public BigDecimal getMonto() { return monto; }
    public EstadoPago getEstado() { return estado; }
    public String getMotivo() { return motivo; }
    public String getReferenciaBanco() { return referenciaBanco; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
