package com.cooperativa.pagos.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String inicio() {
        return """
                <h1>Autorizacion de pagos - Cooperativa</h1>
                <ul>
                  <li><a href="/api/health">/api/health</a> (estado del servicio)</li>
                  <li><a href="/swagger-ui/index.html">/swagger-ui/index.html</a> (probar la API con botones)</li>
                </ul>
                """;
    }
}
