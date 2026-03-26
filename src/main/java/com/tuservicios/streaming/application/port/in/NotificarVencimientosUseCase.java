package com.tuservicios.streaming.application.port.in;

import reactor.core.publisher.Mono;

public interface NotificarVencimientosUseCase {
   Mono<Void> ejecutar();
}