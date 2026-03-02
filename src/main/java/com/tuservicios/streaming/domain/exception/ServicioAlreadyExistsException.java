package com.tuservicios.streaming.domain.exception;


public class ServicioAlreadyExistsException extends RuntimeException {

   public ServicioAlreadyExistsException(Long id) {
      super("El servicio con ID '" + id + "' ya existe");
   }
}