package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActualizarCorreoRequest(

      @NotBlank(message = "El correo es obligatorio")
      @Email(message = "Debe ser un correo válido")
      String correo
) {}
