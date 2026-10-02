package com.cooperativa.pagos.repositorio;

import com.cooperativa.pagos.dominio.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {
}
