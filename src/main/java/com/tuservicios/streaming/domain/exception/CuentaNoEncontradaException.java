package com.tuservicios.streaming.domain.exception;

public class CuentaNoEncontradaException extends RuntimeException {

   public CuentaNoEncontradaException(String cuentaId) {
      super("Cuenta no encontrada");
   }
}
