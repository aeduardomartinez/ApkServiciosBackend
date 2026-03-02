package com.tuservicios.streaming.application.port.in;

import java.time.LocalDate;

import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CuentaUseCase {

   /**
    * Crea una nueva cuenta maestra para un servicio (Netflix, Disney, etc.)
    */
   Mono<Cuenta> crearCuenta(Long servicioId, String clave, String correo, LocalDate inicio, LocalDate fin);

   Mono<Cuenta> obtenerCuenta(Long cuentaId);

   // Crear perfiles base de la cuenta
   Mono<Cuenta> crearPerfilesIniciales(Long cuentaId);

   /**
    * Requerimiento 3: Asocia un nuevo cliente a un cupo disponible en la cuenta.
    */
   // CuentaUseCase.java — cambiar firma de asociarUsuario
   Mono<Cuenta> asociarUsuario(Long cuentaId, Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
   /**
    * Requerimiento 4: Edita la información de un usuario ya asociado.
    */

   Mono<Cuenta> editarPerfil(Long cuentaId, Long perfilId,
         Long clienteId,
         LocalDate fechaInicio, LocalDate fechaFin);

   Flux<Cuenta> listarTodasLasCuentas();

   /**
    * Requerimiento 5: Libera un cupo sin borrar el historial.
    */
   Mono<Cuenta> liberarPerfil(Long cuentaId, Long perfilId);

   /**
    * Requerimiento 6: Búsqueda global de usuarios y sus estados.
    */
   Flux<Cuenta> buscarPorNombreCliente(String nombre);

   Flux<Cuenta> listarCuentas(Long servicio);
}