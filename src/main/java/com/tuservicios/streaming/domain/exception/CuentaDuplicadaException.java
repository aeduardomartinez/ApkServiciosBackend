package com.tuservicios.streaming.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear una cuenta
 * con un correo electrónico que ya existe en el sistema.
 */
public class CuentaDuplicadaException extends RuntimeException {

   public CuentaDuplicadaException(String mensaje) {
      super(mensaje);
   }

   public CuentaDuplicadaException(String correo, Throwable causa) {
      super("Ya existe una cuenta con el correo: " + correo, causa);
   }
}
