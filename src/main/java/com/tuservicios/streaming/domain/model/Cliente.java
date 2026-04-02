package com.tuservicios.streaming.domain.model;

import java.util.Objects;

public record Cliente(Long id, String nombreCompleto, String telefono) {

   public Cliente {
      Objects.requireNonNull(nombreCompleto, "Nombre no puede ser null");
      Objects.requireNonNull(telefono, "Teléfono no puede ser null");

      if (nombreCompleto.isBlank()) {
         throw new IllegalArgumentException("Nombre no puede estar vacío");
      }

      if (telefono.isBlank()) {
         throw new IllegalArgumentException("Teléfono no puede estar vacío");
      }
   }
}