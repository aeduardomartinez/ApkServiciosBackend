package com.tuservicios.streaming.infrastructure.adapter.out.persistence;


import com.tuservicios.streaming.application.port.out.ServicioRepositoryPort;
import com.tuservicios.streaming.domain.exception.ServicioAlreadyExistsException;
import com.tuservicios.streaming.domain.exception.ServicioNotFoundException;
import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper.ServicioPersistenceMapper;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.ServiciosEntity;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository.ServicioReactiveRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public class ServicioRepositoryAdapter implements ServicioRepositoryPort {

   private final ServicioReactiveRepository repo;

   private final ServicioPersistenceMapper mapper;


   public ServicioRepositoryAdapter(ServicioReactiveRepository repo, ServicioPersistenceMapper mapper) {
      this.repo = repo;
      this.mapper = mapper;
   }

   @Override
   public Mono<Servicio> save(Servicio servicio) {
      ServiciosEntity entity = mapper.toEntity(servicio);
      return repo.save(entity)
                       .map(mapper::toDomain)
            .onErrorMap(e -> {
               if(e.getMessage() != null &&
                     (e.getMessage().contains("23505") || e.getMessage().contains("duplicate key"))){
                  return new ServicioAlreadyExistsException(servicio.getId());
               }
                  return e;
            });
   }

   @Override
   public Flux<Servicio> findAll() {
      return repo.findAll().map(mapper::toDomain);
   }

   @Override
   public Mono<Servicio> findByNombre(String nombre) {

      return repo
            .findByNombre(nombre)
            .map(mapper::toDomain);
   }


   @Override
   public Mono<Servicio> findRawById(Long id) {
//      return repo.findById(String.valueOf(id)) // Este es el ReactiveCrudRepository
//                 .map(mapper::toDomain);
      return repo.findById(id).map(mapper::toDomain);
   }
   @Override
   public Mono<Servicio> findById(Long id) {

      return repo.findById(id).map(mapper::toDomain).switchIfEmpty(Mono.error(new ServicioNotFoundException("Servicio con id " + id + " no encontrado")));
   }
}
