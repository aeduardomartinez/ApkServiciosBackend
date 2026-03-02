package com.tuservicios.streaming.application.service;


import com.tuservicios.streaming.application.port.in.ClienteRegistradoUseCase;
import com.tuservicios.streaming.application.port.out.ClienteRegistradoRepositoryPort;
import com.tuservicios.streaming.domain.exception.ClienteNoEncontradoException;
import com.tuservicios.streaming.domain.exception.ClienteDuplicadoException;
import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteRegistradoService implements ClienteRegistradoUseCase {

   private final ClienteRegistradoRepositoryPort repo;

   @Override
   public Mono<ClienteRegistrado> crearCliente(String nombre, String apellido, String telefono) {
      log.info("Creando cliente: {} {}", nombre, apellido);

      return repo.existsByTelefono(telefono)
                 .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                       return Mono.error(new ClienteDuplicadoException(
                             "Ya existe un cliente con el teléfono: " + telefono));
                    }
                    ClienteRegistrado nuevo = new ClienteRegistrado(null, nombre, apellido, telefono);
                    return repo.save(nuevo);
                 });
   }

   @Override
   public Mono<ClienteRegistrado> obtenerCliente(Long id) {
      return repo.findById(id)
                 .switchIfEmpty(Mono.error(
                       new ClienteNoEncontradoException("Cliente con ID " + id + " no encontrado")));
   }

   @Override
   public Flux<ClienteRegistrado> listarClientes() {
      return repo.findAll();
   }

   @Override
   public Mono<ClienteRegistrado> actualizarCliente(Long id, String nombre, String apellido, String telefono) {
      log.info("Actualizando cliente id={}", id);

      return repo.findById(id)
                 .switchIfEmpty(Mono.error(
                       new ClienteNoEncontradoException("Cliente con ID " + id + " no encontrado")))
                 .flatMap(existente -> {
                    ClienteRegistrado actualizado = new ClienteRegistrado(id, nombre, apellido, telefono);
                    return repo.update(id, actualizado);
                 });
   }

   @Override
   public Mono<Void> eliminarCliente(Long id) {
      log.info("Eliminando cliente id={}", id);
      return repo.findById(id)
                 .switchIfEmpty(Mono.error(
                       new ClienteNoEncontradoException("Cliente con ID " + id + " no encontrado")))
                 .flatMap(c -> repo.deleteById(id));
   }
}
