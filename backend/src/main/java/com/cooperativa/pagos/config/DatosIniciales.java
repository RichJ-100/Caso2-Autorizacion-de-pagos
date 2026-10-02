package com.cooperativa.pagos.config;

import com.cooperativa.pagos.dominio.Cuenta;
import com.cooperativa.pagos.repositorio.CuentaRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final CuentaRepository cuentas;

    public DatosIniciales(CuentaRepository cuentas) {
        this.cuentas = cuentas;
    }

    @Override
    public void run(String... args) {
        if (cuentas.count() == 0) {
            cuentas.save(new Cuenta("001-0001", "Ana Perez", true,
                    new BigDecimal("2000.00"), new BigDecimal("5000.00")));
            cuentas.save(new Cuenta("001-0002", "Luis Mora", false,
                    new BigDecimal("2000.00"), new BigDecimal("5000.00")));
        }
    }
}
