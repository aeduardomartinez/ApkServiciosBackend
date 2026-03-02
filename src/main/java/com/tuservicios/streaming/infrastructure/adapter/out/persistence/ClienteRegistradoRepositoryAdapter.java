package com.tuservicios.streaming.infrastructure.adapter.out.persistence;

import com.tuservicios.streaming.application.port.out.ClienteRegistradoRepositoryPort;
import com.tuservicios.streaming.domain.exception.ClienteNoEncontradoException;
import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper.ClienteRegistradoPersistenceMapper;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository.ClienteRegistradoReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ClienteRegistradoRepositoryAdapter implements ClienteRegistradoRepositoryPort {

   private final ClienteRegistradoReactiveRepository repo;
   private final ClienteRegistradoPersistenceMapper  mapper;

   @Override
   public Mono<ClienteRegistrado> save(ClienteRegistrado cliente) {
      return repo.save(mapper.toEntity(cliente))
                 .map(mapper::toDomain);
   }

   @Override
   public Mono<ClienteRegistrado> findById(Long id) {
      return repo.findById(id)
                 .map(mapper::toDomain);
   }

   @Override
   public Flux<ClienteRegistrado> findAll() {
      return repo.findAll().map(mapper::toDomain);
   }

   @Override
   public Mono<ClienteRegistrado> update(Long id, ClienteRegistrado cliente) {
      return repo.updateCliente(id, cliente.nombre(), cliente.apellido(), cliente.telefono())
                 .flatMap(rows -> rows == 1
                       ? repo.findById(id).map(mapper::toDomain)
                       : Mono.error(new ClienteNoEncontradoException("No se pudo actualizar cliente " + id)));
   }

   @Override
   public Mono<Void> deleteById(Long id) {
      return repo.deleteById(id);
   }

   @Override
   public Mono<Boolean> existsByTelefono(String telefono) {
      return repo.existsByTelefono(telefono);
   }
}