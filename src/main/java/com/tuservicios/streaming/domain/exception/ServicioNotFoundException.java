package com.tuservicios.streaming.domain.exception;


public class ServicioNotFoundException extends RuntimeException {

//   public ServicioNombreFoundException(String nombre) {
//      super("Servicio no encontrado: " + nombre);
//   }

   public ServicioNotFoundException(String id) {
      super("Servicio no encontrado: " + id);
   }
}