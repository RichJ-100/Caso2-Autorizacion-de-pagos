package com.cooperativa.pagos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    private String numero;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal limitePorPago;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal limiteDiario;

    protected Cuenta() {
    }

    public Cuenta(String numero, String titular, boolean activa,
                  BigDecimal limitePorPago, BigDecimal limiteDiario) {
        this.numero = numero;
        this.titular = titular;
        this.activa = activa;
        this.limitePorPago = limitePorPago;
        this.limiteDiario = limiteDiario;
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public boolean isActiva() { return activa; }
    public BigDecimal getLimitePorPago() { return limitePorPago; }
    public BigDecimal getLimiteDiario() { return limiteDiario; }
}
