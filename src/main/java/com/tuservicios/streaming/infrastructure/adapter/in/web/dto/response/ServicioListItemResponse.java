package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;


public record ServicioListItemResponse(
      Long id,
      String nombreServicio,
      int maxPerfilesBase,
      int maxPerfilesExtra,
      double valorBase,
      double valorPerfil
) {}