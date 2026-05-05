package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarClaveRequest(

      @NotBlank(message = "La clave es obligatoria")
      String clave
) {}
