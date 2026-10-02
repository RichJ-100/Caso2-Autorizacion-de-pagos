package com.cooperativa.pagos.integracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);

    @RabbitListener(queues = NotificacionPublisher.COLA)
    public void recibir(String mensaje) {
        log.info("Notificacion al socio: {}", mensaje);
    }
}
