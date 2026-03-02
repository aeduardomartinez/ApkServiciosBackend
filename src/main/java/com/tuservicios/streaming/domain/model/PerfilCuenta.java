package com.tuservicios.streaming.domain.model;

import java.time.LocalDate;
import com.tuservicios.streaming.domain.model.enums.EstadoPerfil;
import lombok.Getter;

@Getter
public class PerfilCuenta {

   private Long       idPerfil;
   private Cliente    cliente;      // solo nombre + teléfono
   private LocalDate  fechaInicio;  // fechas de suscripción del perfil
   private LocalDate  fechaFin;
   private EstadoPerfil estado;

   protected PerfilCuenta() {}

   public PerfilCuenta(Long idPerfil, Cliente cliente,
         LocalDate fechaInicio, LocalDate fechaFin,
         EstadoPerfil estado) {
      this.idPerfil    = idPerfil;
      this.cliente     = cliente;
      this.fechaInicio = fechaInicio;
      this.fechaFin    = fechaFin;
      this.estado      = estado;
   }

   public void asignar(Cliente c, LocalDate inicio, LocalDate fin) {
      if (estado != EstadoPerfil.LIBRE) {
         throw new IllegalStateException("El perfil no está disponible. Estado: " + estado);
      }
      this.cliente     = c;
      this.fechaInicio = inicio;
      this.fechaFin    = fin;
      this.estado      = EstadoPerfil.ACTIVO;
   }

   public void actualizar(Cliente c, LocalDate inicio, LocalDate fin) {
      if (c == null) throw new IllegalArgumentException("El cliente no puede ser null");
      this.cliente     = c;
      this.fechaInicio = inicio;
      this.fechaFin    = fin;
   }

   public void liberar() {
      this.cliente     = null;
      this.fechaInicio = null;
      this.fechaFin    = null;
      this.estado      = EstadoPerfil.LIBRE;
   }

   public boolean verificarVencimiento() {
      if (estado == EstadoPerfil.LIBRE || fechaFin == null) return false;
      if (LocalDate.now().isAfter(fechaFin)) {
         this.estado = EstadoPerfil.VENCIDO;
         return true;
      }
      return false;
   }

   public boolean estaDisponible()    { return estado == EstadoPerfil.LIBRE; }
   public boolean tieneClienteActivo(){ return cliente != null && estado == EstadoPerfil.ACTIVO; }
   public void setEstado(EstadoPerfil estado) { this.estado = estado; }
}