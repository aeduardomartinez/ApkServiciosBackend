package com.tuservicios.streaming.application.port.in;

import com.tuservicios.streaming.domain.model.Servicio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiciosUseCase {
   Mono<Servicio> crearServicio(Servicio servicio);
   Flux<Servicio> listarServicios();              // todos
   Mono<Servicio> obtenerServicio(Long id);
}
