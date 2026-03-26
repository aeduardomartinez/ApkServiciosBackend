package com.tuservicios.streaming.application.port.out;

import com.tuservicios.streaming.application.port.out.dto.NotificacionRequest;

import reactor.core.publisher.Mono;

public interface NotificacionPort {

   Mono<String> enviar(NotificacionRequest request);

}