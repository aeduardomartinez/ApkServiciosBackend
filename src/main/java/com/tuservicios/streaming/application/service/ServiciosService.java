package com.tuservicios.streaming.application.service;


import org.springframework.stereotype.Service;

import com.tuservicios.streaming.application.port.in.ServiciosUseCase;
import com.tuservicios.streaming.application.port.out.ServicioRepositoryPort;
import com.tuservicios.streaming.domain.exception.ServicioAlreadyExistsException;
import com.tuservicios.streaming.domain.model.Servicio;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServiciosService implements ServiciosUseCase {

   private final ServicioRepositoryPort repo;

   public ServiciosService(ServicioRepositoryPort repository) {
      this.repo = repository;
   }

   @Override
   public Mono<Servicio> crearServicio(Servicio servicio) {
      return repo.findByNombre(servicio.getNombreServicio())  // ← busca por nombre
                 .flatMap(existente -> Mono.<Servicio>error(
                       new ServicioAlreadyExistsException(existente.getId())))
                 .switchIfEmpty(Mono.defer(() -> repo.save(servicio)));
   }

   @Override
   public Flux<Servicio> listarServicios() {
      return repo.findAll();
   }

   @Override
   public Mono<Servicio> obtenerServicio(Long id) {
      return repo.findById(id);
   }
}