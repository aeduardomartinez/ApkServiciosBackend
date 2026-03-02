package com.tuservicios.streaming.domain.exception;

public class CuposExtraExcedidosException extends RuntimeException {

   public CuposExtraExcedidosException(String servicio) {
      super("Límite de cupos extra alcanzado para: " + servicio);
   }
}