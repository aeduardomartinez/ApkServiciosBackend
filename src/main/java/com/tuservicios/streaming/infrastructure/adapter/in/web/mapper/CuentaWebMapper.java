package com.tuservicios.streaming.infrastructure.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;
import com.tuservicios.streaming.domain.model.PerfilCuenta;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.CuentaRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.CuentaResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.PerfilResponse;

import java.util.List;
import java.util.stream.IntStream;

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
            c.getClave(),
            c.getFechaInicio(),
            c.getFechaFin(),
            mapPerfiles(c)
      );
   }

   private List<PerfilResponse> mapPerfiles(Cuenta c) {
      int maxBase = c.getServicio().getMaxPerfilesBase();
      var perfiles = c.getPerfiles();
      
      return IntStream.range(0, perfiles.size())
            .mapToObj(i -> toPerfilResponse(perfiles.get(i), i >= maxBase))
            .toList();
   }

   private PerfilResponse toPerfilResponse(PerfilCuenta p, boolean isExtra) {
      Cliente cliente = p.getCliente();

      return new PerfilResponse(
            p.getIdPerfil(),
            cliente != null ? cliente.id() : null,
            cliente != null ? cliente.nombreCompleto() : null,
            cliente != null ? cliente.telefono() : null,
            p.getFechaInicio(),
            p.getFechaFin(),
            p.getEstado().name(),
            isExtra,
            p.getCorreoExtra(),
            p.getClaveExtra()
      );
   }
}
