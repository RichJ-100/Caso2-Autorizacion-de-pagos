package com.cooperativa.pagos.repositorio;

import com.cooperativa.pagos.dominio.EstadoPago;
import com.cooperativa.pagos.dominio.Pago;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, UUID> {

    Optional<Pago> findByClaveIdempotencia(String claveIdempotencia);

    List<Pago> findByEstado(EstadoPago estado);

    @Query("select sum(p.monto) from Pago p "
            + "where p.cuenta = :cuenta and p.estado in :estados and p.creadoEn >= :desde")
    BigDecimal sumaDesde(@Param("cuenta") String cuenta,
                         @Param("estados") Collection<EstadoPago> estados,
                         @Param("desde") Instant desde);
}
