package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClienteRegistradoRequest(

      @NotBlank(message = "El nombre es obligatorio")
      String nombre,

      @NotBlank(message = "El apellido es obligatorio")
      String apellido,

      @NotBlank(message = "El teléfono es obligatorio")
      @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido")
      String telefono
) {}