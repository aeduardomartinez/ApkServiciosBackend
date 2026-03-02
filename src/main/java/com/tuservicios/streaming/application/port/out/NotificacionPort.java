package com.tuservicios.streaming.application.port.out;

import reactor.core.publisher.Mono;

public interface NotificacionPort {
   Mono<Void> enviarMensajeWhatsApp(String telefono, String mensaje);
}