package com.tuservicios.streaming.domain.model;

import java.util.Objects;

public record ClienteRegistrado(Long id, String nombre, String apellido, String telefono) {

   public ClienteRegistrado {
      Objects.requireNonNull(nombre,    "Nombre no puede ser null");
      Objects.requireNonNull(apellido,  "Apellido no puede ser null");
      Objects.requireNonNull(telefono,  "Teléfono no puede ser null");

      if (nombre.isBlank())   throw new IllegalArgumentException("Nombre no puede estar vacío");
      if (apellido.isBlank()) throw new IllegalArgumentException("Apellido no puede estar vacío");
      if (telefono.isBlank()) throw new IllegalArgumentException("Teléfono no puede estar vacío");
   }

   /** Nombre completo para usar en value object Cliente */
   public String nombreCompleto() {
      return nombre + " " + apellido;
   }
}