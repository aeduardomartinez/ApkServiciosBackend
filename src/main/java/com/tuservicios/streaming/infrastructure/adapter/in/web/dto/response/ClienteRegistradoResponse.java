package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;

public record ClienteRegistradoResponse(
      Long   id,
      String nombre,
      String apellido,
      String telefono,
      String nombreCompleto
) {}