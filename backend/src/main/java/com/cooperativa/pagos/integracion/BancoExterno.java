package com.cooperativa.pagos.integracion;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoExterno {

    ResultadoBanco autorizar(UUID pagoId, String cuenta, BigDecimal monto);

    ResultadoBanco consultar(UUID pagoId);
}
