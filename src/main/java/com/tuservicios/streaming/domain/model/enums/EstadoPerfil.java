package com.tuservicios.streaming.domain.model.enums;

public enum EstadoPerfil {
   ACTIVO,
   VENCIDO,
   LIBRE;

   public static EstadoPerfil fromDb(String raw) {
      if (raw == null || raw.isBlank()) {
         return LIBRE; // ✅ default de negocio
      }
      return EstadoPerfil.valueOf(raw.trim().toUpperCase());
   }

   }