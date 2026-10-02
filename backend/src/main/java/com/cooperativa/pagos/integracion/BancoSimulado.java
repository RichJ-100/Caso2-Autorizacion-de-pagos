package com.cooperativa.pagos.integracion;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Banco de mentira para la demo.
 * Monto 999.99 simula que el banco no responde a tiempo.
 * Monto 13.13 simula que el banco rechaza el pago.
 * Cualquier otro monto es aprobado.
 */
@Component
public class BancoSimulado implements BancoExterno {

    private static final BigDecimal MONTO_TIMEOUT = new BigDecimal("999.99");
    private static final BigDecimal MONTO_RECHAZO = new BigDecimal("13.13");

    @Override
    public ResultadoBanco autorizar(UUID pagoId, String cuenta, BigDecimal monto) {
        if (monto.compareTo(MONTO_TIMEOUT) == 0) {
            throw new BancoNoDisponibleException("Timeout simulado del banco");
        }
        String referencia = "BCO-" + pagoId.toString().substring(0, 8);
        if (monto.compareTo(MONTO_RECHAZO) == 0) {
            return new ResultadoBanco(false, referencia);
        }
        return new ResultadoBanco(true, referencia);
    }

    @Override
    public ResultadoBanco consultar(UUID pagoId) {
        return new ResultadoBanco(true, "BCO-" + pagoId.toString().substring(0, 8));
    }
}
