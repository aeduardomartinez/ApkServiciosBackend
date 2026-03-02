package com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper;

import com.tuservicios.streaming.domain.model.ClienteRegistrado;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.ClienteRegistradoEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteRegistradoPersistenceMapper {

   public ClienteRegistrado toDomain(ClienteRegistradoEntity e) {
      return new ClienteRegistrado(e.getId(), e.getNombre(), e.getApellido(), e.getTelefono());
   }

   public ClienteRegistradoEntity toEntity(ClienteRegistrado c) {
      return new ClienteRegistradoEntity(c.id(), c.nombre(), c.apellido(), c.telefono());
   }
}