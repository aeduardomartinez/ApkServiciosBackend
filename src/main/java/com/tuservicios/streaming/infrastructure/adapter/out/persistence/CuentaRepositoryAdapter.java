package com.tuservicios.streaming.infrastructure.adapter.out.persistence;

import java.time.LocalDate;

import com.tuservicios.streaming.application.port.out.CuentaRepositoryPort;
import com.tuservicios.streaming.application.port.out.ServicioRepositoryPort;
import com.tuservicios.streaming.domain.exception.CuentaNoEncontradaException;
import com.tuservicios.streaming.domain.exception.CuposInsuficientesException;
import com.tuservicios.streaming.domain.exception.PerfilNoEncontradoException;
import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper.CuentaPersistenceMapper;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.PerfilEntity;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository.CuentaReactiveRepository;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository.PerfilReactiveRepository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

   private final CuentaReactiveRepository cuentaRepo;

   private final PerfilReactiveRepository perfilRepo;

   private final ServicioRepositoryPort servicioRepo;

   private final CuentaPersistenceMapper mapper;

   @Override
   public Mono<Cuenta> save(Cuenta cuenta) {

      CuentaEntity entity = mapper.toEntity(cuenta);

      return cuentaRepo.save(entity).map(saved -> {
         cuenta.setId(saved.getId()); // 🔥 recupera el ID generado
         return cuenta;
      });
   }

   @Override
   public Mono<Void> crearPerfilesIniciales(Long cuentaId, int cantidad) {
      if (cantidad <= 0) {
         return Mono.empty();
      }

      return perfilRepo.existsByCuentaId(cuentaId).flatMap(existe -> {
         if (Boolean.TRUE.equals(existe)) {
            return Mono.empty();
         }

         return Flux.range(0, cantidad).concatMap(i -> perfilRepo.insertarLibre(cuentaId)).then();
      });
   }

   @Override
   public Mono<Cuenta> findById(Long id) {
      return loadAggregate(id);
   }

   @Override
   public Mono<Void> asignarClienteEnPerfilLibre(Long cuentaId, Long clienteId, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin) {
      return perfilRepo
            .findFirstByCuentaIdAndEstadoOrderByIdPerfilAsc(cuentaId, "LIBRE")
            .switchIfEmpty(Mono.error(new CuposInsuficientesException("No hay perfiles libres")))
            .flatMap(libre -> perfilRepo.asignarEnPerfil(cuentaId, libre.getIdPerfil(), clienteId, cliente.nombreCompleto(), cliente.telefono(),
                  fechaInicio, fechaFin))
            .flatMap(rows -> rows == 1 ? Mono.empty() : Mono.error(new IllegalStateException("Asignación fallida")));
   }

   @Override
   public Mono<Void> actualizarClienteEnPerfil(Long cuentaId, Long perfilId, Long clienteId, Cliente cliente, LocalDate fechaInicio,
         LocalDate fechaFin) {
      return expectOneRowUpdated(
            perfilRepo.actualizarCliente(cuentaId, perfilId, clienteId, cliente.nombreCompleto(), cliente.telefono(), fechaInicio, fechaFin),
            new PerfilNoEncontradoException(perfilId.toString()));
   }

   @Override
   public Mono<Void> liberarPerfil(Long cuentaId, Long perfilId) {
      return expectOneRowUpdated(perfilRepo.liberarPerfil(cuentaId, perfilId), new PerfilNoEncontradoException(perfilId.toString()));
   }

   private Mono<Void> expectOneRowUpdated(Mono<Integer> op, RuntimeException notFound) {
      return op.flatMap(rows -> rows == 1 ? Mono.<Void>empty() : Mono.error(notFound));
   }

   @Override
   public Flux<Cuenta> findAll() {

      log.debug("Listando todas las cuentas");

      return cuentaRepo
            .findAll()
            .map(CuentaEntity::getId)
            .flatMap(this::loadAggregate)
            .doOnComplete(() -> log.debug("Listado de cuentas completado"));
   }

   private Mono<Cuenta> loadAggregate(Long cuentaId) {
      return cuentaRepo
            .findById(cuentaId)
            .switchIfEmpty(Mono.error(new CuentaNoEncontradaException("Cuenta con ID " + cuentaId + " no encontrada")))
            .flatMap(cuentaEntity -> Mono
                  .zip(servicioRepo.findById(cuentaEntity.getServicioId()), perfilRepo.findByCuentaId(cuentaId).collectList())
                  .map(tuple -> mapper.toDomain(cuentaEntity, tuple.getT1(), tuple.getT2())));
   }

   @Override
   public Flux<Cuenta> findByClienteNombre(String nombre) {

      log.debug("Buscando cuentas por nombre de cliente: {}", nombre);

      if (nombre == null || nombre.isBlank()) {
         return Flux.empty();
      }

      return perfilRepo.findByNombreClienteContaining(nombre.trim())
                       .map(PerfilEntity::getCuentaId)
                       .distinct()
                       .flatMap(this::loadAggregate);
   }

   @Override
   public Flux<Cuenta> findByServicioId(Long servicioId) {
      return cuentaRepo.findByServicioId(servicioId)  // query que filtra cuentas por servicio_id
                       .map(CuentaEntity::getId).flatMap(this::loadAggregate);
   }

}