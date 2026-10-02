package com.cooperativa.pagos.config;

import com.cooperativa.pagos.integracion.NotificacionPublisher;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue colaNotificaciones() {
        return new Queue(NotificacionPublisher.COLA, true);
    }
}
