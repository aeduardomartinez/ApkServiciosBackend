package com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.ServiciosEntity;

@Component
public class ServicioPersistenceMapper {

   public Servicio toDomain(ServiciosEntity e) {
      if (e == null) return null;
      return Servicio.reconstruir(
            e.getId(),
            e.getNombre(),
            e.getMaxPerfilesBase() != null ? e.getMaxPerfilesBase() : 0,
            e.getMaxPerfilesExtra() != null ? e.getMaxPerfilesExtra() : 0,
            e.getValorBase() != null ? e.getValorBase().doubleValue() : 0.0,
            e.getValorPerfil() != null ? e.getValorPerfil().doubleValue() : 0.0
      );
   }

   public ServiciosEntity toEntity(Servicio s) {
      if (s == null) {
         return null;
      }
      return new ServiciosEntity(
            s.getId(),
            s.getNombreServicio(),
            s.getMaxPerfilesBase(),
            s.getMaxPerfilesExtra(),
            BigDecimal.valueOf(s.getValorBase()),
            BigDecimal.valueOf(s.getValorPerfil())

      );
   }
}