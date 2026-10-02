package com.cooperativa.pagos.servicio;

import com.cooperativa.pagos.dominio.Auditoria;
import com.cooperativa.pagos.repositorio.AuditoriaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repositorio;

    public AuditoriaService(AuditoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void registrar(UUID pagoId, String evento, String detalle) {
        repositorio.save(new Auditoria(pagoId, evento, detalle));
    }

    public List<Auditoria> listar(UUID pagoId) {
        return repositorio.findByPagoIdOrderByIdAsc(pagoId);
    }
}
