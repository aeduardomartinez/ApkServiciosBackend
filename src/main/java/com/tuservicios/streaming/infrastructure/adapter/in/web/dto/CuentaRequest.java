package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CuentaRequest(

      long servicioId,

      @NotBlank(message = "la clave es obligatorio")
      String clave,

      @NotBlank(message = "El correo principal es obligatorio")
      @Email(message = "Debe ser un correo válido")
      String correo,

      @NotNull(message = "La fecha de inicio es obligatoria")
      LocalDate fechaInicio,

      @NotNull(message = "La fecha de vencimiento es obligatoria")
      LocalDate fechaFin
) {}