package com.cooperativa.pagos.integracion;

import com.cooperativa.pagos.dominio.Pago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificacionPublisher {

    public static final String COLA = "pagos.notificaciones";

    private static final Logger log = LoggerFactory.getLogger(NotificacionPublisher.class);

    private final RabbitTemplate rabbit;

    public NotificacionPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publicar(Pago pago) {
        try {
            rabbit.convertAndSend(COLA, "pago=" + pago.getId() + " estado=" + pago.getEstado());
        } catch (Exception e) {
            log.warn("No se pudo publicar la notificacion (el pago no se afecta): {}", e.getMessage());
        }
    }
}
