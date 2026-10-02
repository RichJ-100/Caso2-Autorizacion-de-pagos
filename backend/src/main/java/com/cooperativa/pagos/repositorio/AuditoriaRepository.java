package com.cooperativa.pagos.repositorio;

import com.cooperativa.pagos.dominio.Auditoria;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByPagoIdOrderByIdAsc(UUID pagoId);
}
