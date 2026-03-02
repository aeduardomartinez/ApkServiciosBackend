package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record CuentaResponse(Long id, Long servicio, String ServicioNombre, String correo, LocalDate fechaInicio, LocalDate fechaFin, List<PerfilResponse> perfiles) {

   public CuentaResponse {
   }
}