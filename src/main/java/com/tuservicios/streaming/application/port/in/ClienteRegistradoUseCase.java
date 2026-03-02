package com.tuservicios.streaming.application.port.in;


import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRegistradoUseCase {

   Mono<ClienteRegistrado> crearCliente(String nombre, String apellido, String telefono);

   Mono<ClienteRegistrado> obtenerCliente(Long id);

   Flux<ClienteRegistrado> listarClientes();

   Mono<ClienteRegistrado> actualizarCliente(Long id, String nombre, String apellido, String telefono);

   Mono<Void> eliminarCliente(Long id);
}