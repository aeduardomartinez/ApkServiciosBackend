package com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.ServiciosEntity;

import reactor.core.publisher.Mono;

@Repository
public interface ServicioReactiveRepository
      extends R2dbcRepository<ServiciosEntity, String> {

   Mono<ServiciosEntity> findById(Long id);
   Mono<ServiciosEntity> findByNombre(String nombre);

}
