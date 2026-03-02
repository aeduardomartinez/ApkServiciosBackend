package com.tuservicios.streaming.application.port.out;

import java.time.LocalDate;

import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CuentaRepositoryPort {

   Mono<Cuenta> findById(Long id);

   Mono<Cuenta> save(Cuenta cuenta);

   Flux<Cuenta> findAll();

   Flux<Cuenta> findByClienteNombre(String nombre); // Para búsqueda global (Req 6)

   Mono<Void> liberarPerfil(Long cuentaId, Long perfilId);

   // Recibe clienteId + fechas por separado
   Mono<Void> asignarClienteEnPerfilLibre(Long cuentaId, Long clienteId, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin);

   Mono<Void> actualizarClienteEnPerfil(Long cuentaId, Long perfilId, Long clienteId, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin);

   Mono<Void> crearPerfilesIniciales(Long cuentaId, int cantidad);

   Flux<Cuenta> findByServicioId(Long servicioId);

}
