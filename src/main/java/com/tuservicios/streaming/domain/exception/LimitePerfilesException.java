package com.tuservicios.streaming.domain.exception;

public class LimitePerfilesException extends RuntimeException {

   public LimitePerfilesException() {
      super("Límite de perfiles extra alcanzado");
   }
}
