package com.tuservicios.streaming.infrastructure.adapter.out.persistence.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;
import com.tuservicios.streaming.domain.model.PerfilCuenta;
import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.domain.model.enums.EstadoPerfil;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.PerfilEntity;

@Component
public class CuentaPersistenceMapper {

   // ===== DOMINIO → ENTITY =====
   public CuentaEntity toEntity(Cuenta c) {
      return new CuentaEntity(c.getId(), c.getServicio().getId(), c.getClave(), c.getCorreoPrincipal(), c.getFechaInicio(), c.getFechaFin(),
            c.getCuposExtraContratados());
   }

   // ===== ENTITY → DOMINIO =====

   public Cuenta toDomain(CuentaEntity e, Servicio servicio, List<PerfilEntity> perfilesE) {

      Cuenta cuenta = new Cuenta(e.getId(), servicio, e.getClave(), e.getCorreoPrincipal(), e.getFechaInicio(), e.getFechaFin());

      cuenta.setCuposExtraContratados(Optional.ofNullable(e.getCuposExtraContratados()).orElse(0));
      // Transformar y asignar toda la lista de una vez
      List<PerfilCuenta> perfiles = perfilesE.stream().map(this::perfilToDomain).collect(Collectors.toList());

      cuenta.setPerfiles(perfiles);
      return cuenta;
   }

   private PerfilCuenta perfilToDomain(PerfilEntity e) {
      EstadoPerfil estado = EstadoPerfil.fromDb(e.getEstado());

      Cliente cliente = null;
      if ((estado == EstadoPerfil.ACTIVO || estado == EstadoPerfil.VENCIDO) && e.getNombreCliente() != null) {
         cliente = new Cliente(e.getClienteId(), e.getNombreCliente(), e.getTelefonoCliente());
      }

      return new PerfilCuenta(e.getIdPerfil(), cliente, e.getFechaInicio(),  // fechas del perfil, no del cliente
            e.getFechaFin(), estado, e.getCorreoExtra(), e.getClaveExtra());
   }

   private String clienteNombre(PerfilCuenta p) {
      return p.getCliente() != null ? p.getCliente().nombreCompleto() : "SIN_CLIENTE";
   }

   private String clienteTelefono(PerfilCuenta p) {
      return p.getCliente() != null ? p.getCliente().telefono() : null;
   }

   private String estado(PerfilCuenta p) {
      return p.getEstado() != null ? p.getEstado().name() : "LIBRE";
   }
}
