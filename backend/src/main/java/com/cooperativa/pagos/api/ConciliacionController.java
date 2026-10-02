package com.cooperativa.pagos.api;

import com.cooperativa.pagos.servicio.ConciliacionService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conciliacion")
public class ConciliacionController {

    private final ConciliacionService conciliacion;

    public ConciliacionController(ConciliacionService conciliacion) {
        this.conciliacion = conciliacion;
    }

    @PostMapping("/ejecutar")
    public Map<String, Integer> ejecutar() {
        return Map.of("resueltos", conciliacion.conciliar());
    }
}
