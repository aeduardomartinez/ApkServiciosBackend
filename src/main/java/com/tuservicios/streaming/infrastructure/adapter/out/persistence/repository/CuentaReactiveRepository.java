package com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.CuentaEntity;

import reactor.core.publisher.Flux;

@Repository
public interface CuentaReactiveRepository extends R2dbcRepository<CuentaEntity, Long> {

   // Spring Data genera la implementación reactiva automáticamente
   Flux<CuentaEntity> findByCorreoPrincipal(String correo);
   Flux<CuentaEntity> findByServicioId(Long servicioId);

}