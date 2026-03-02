package com.tuservicios.streaming.application.port.out;


import com.tuservicios.streaming.domain.model.Servicio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServicioRepositoryPort {

   Mono<Servicio> findById(Long id);

   Mono<Servicio> findByNombre(String nombre);

   Mono<Servicio> findRawById(Long id);

   Flux<Servicio> findAll();

   Mono<Servicio> save(Servicio servicio);



}