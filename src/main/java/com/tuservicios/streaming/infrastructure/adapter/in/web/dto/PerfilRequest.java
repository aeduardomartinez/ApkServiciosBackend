package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public record PerfilRequest(

      @NotNull(message = "El ID del cliente es obligatorio")
      Long clienteId,

      @NotNull(message = "La fecha de inicio es obligatoria")
      LocalDate fechaInicio,

      @NotNull(message = "La fecha de fin es obligatoria")
      LocalDate fechaFin
) {}