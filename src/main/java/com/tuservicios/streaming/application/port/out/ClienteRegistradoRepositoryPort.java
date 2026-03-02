package com.tuservicios.streaming.application.port.out;

import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRegistradoRepositoryPort {

   Mono<ClienteRegistrado> save(ClienteRegistrado cliente);

   Mono<ClienteRegistrado> findById(Long id);

   Flux<ClienteRegistrado> findAll();

   Mono<ClienteRegistrado> update(Long id, ClienteRegistrado cliente);

   Mono<Void> deleteById(Long id);

   Mono<Boolean> existsByTelefono(String telefono);
}