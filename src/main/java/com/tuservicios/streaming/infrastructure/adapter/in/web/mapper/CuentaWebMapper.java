package com.tuservicios.streaming.infrastructure.adapter.in.web.mapper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;
import com.tuservicios.streaming.domain.model.PerfilCuenta;
import com.tuservicios.streaming.domain.model.enums.EstadoPerfil;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.CuentaRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.CuentaResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.PerfilResponse;

@Component
public class CuentaWebMapper {

   // DTO → DOMINIO
   public Cuenta toDomain(CuentaRequest r) {
      return new Cuenta(null, null, r.clave(), r.correo(), r.fechaInicio(), r.fechaFin());
   }

   // DOMINIO → DTO
   public CuentaResponse toResponse(Cuenta c) {
      return new CuentaResponse(
            c.getId(),
            c.getServicio().getId(),
            c.getServicio().getNombreServicio(),
            c.getCorreoPrincipal(),
            c.getFechaInicio(),
            c.getFechaFin(),
            c.getPerfiles().stream()
             .map(this::toPerfilResponse)
             .toList()
      );
   }

   private PerfilResponse toPerfilResponse(PerfilCuenta p) {
      Cliente cliente = p.getCliente();

      return new PerfilResponse(
            p.getIdPerfil(),
            cliente != null ? cliente.nombreCompleto() : null,
            cliente != null ? cliente.telefono() : null,
            p.getFechaInicio(),   // ← del perfil, no del cliente
            p.getFechaFin(),      // ← del perfil, no del cliente
            p.getEstado().name()
      );
   }
}
