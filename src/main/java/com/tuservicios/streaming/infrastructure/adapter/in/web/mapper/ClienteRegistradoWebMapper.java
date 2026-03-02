package com.tuservicios.streaming.infrastructure.adapter.in.web.mapper;

import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ClienteRegistradoResponse;
import org.springframework.stereotype.Component;

@Component
public class ClienteRegistradoWebMapper {

   public ClienteRegistradoResponse toResponse(ClienteRegistrado c) {
      return new ClienteRegistradoResponse(
            c.id(),
            c.nombre(),
            c.apellido(),
            c.telefono(),
            c.nombreCompleto()
      );
   }
}
