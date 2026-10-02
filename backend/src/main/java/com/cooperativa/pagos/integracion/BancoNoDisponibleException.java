package com.cooperativa.pagos.integracion;

public class BancoNoDisponibleException extends RuntimeException {

    public BancoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
