package com.tuservicios.streaming.domain.exception;

public class PerfilesNoDisponiblesException extends RuntimeException {

   public PerfilesNoDisponiblesException(String s) {
      super("No hay perfiles disponibles");
   }
}