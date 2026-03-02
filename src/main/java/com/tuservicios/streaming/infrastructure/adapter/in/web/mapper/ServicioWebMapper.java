package com.tuservicios.streaming.infrastructure.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ServicioListItemResponse;

@Component
public class ServicioWebMapper {

   public ServicioListItemResponse toListItem(Servicio s) {
      return new ServicioListItemResponse(
            s.getId(),
            s.getNombreServicio(),
            s.getMaxPerfilesBase(),
            s.getMaxPerfilesExtra(),
            s.getValorBase(),
            s.getValorPerfil()
      );
   }
}