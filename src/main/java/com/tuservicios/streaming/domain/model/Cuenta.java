package com.tuservicios.streaming.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tuservicios.streaming.domain.model.enums.EstadoPerfil;

import lombok.Getter;
import lombok.Setter;

/**
 * Agregado raíz que representa una cuenta de servicio de streaming.
 * <p>
 * Invariantes:
 * - Siempre tiene un servicio asociado
 * - El número de perfiles no puede exceder: maxPerfilesBase + cuposExtraContratados
 * - Los cupos extra no pueden superar el límite del servicio
 * - Las fechas de inicio/fin son consistentes
 */
@Getter
public class Cuenta {

   @Setter
   private Long id;

   private Servicio servicio;

   private String clave;

   private String correoPrincipal;

   @Setter
   private List<PerfilCuenta> perfiles = new ArrayList<>();

   private int cuposExtraContratados;

   private LocalDate fechaInicio;

   private LocalDate fechaFin;


   private EstadoPerfil estado;

   // ======================================================
   // =============== CONSTRUCTORES ========================
   // ======================================================

   /**
    * Constructor completo - uso interno y para reconstrucción desde BD
    */
   public Cuenta(Long id, Servicio servicio, String clave, String correoPrincipal, LocalDate fechaInicio, LocalDate fechaFin,
         int cuposExtraContratados, List<PerfilCuenta> perfiles) {

      validarParametrosBasicos(servicio, clave, correoPrincipal, fechaInicio, fechaFin);

      this.id = id;
      this.servicio = servicio;
      this.clave = clave;
      this.correoPrincipal = correoPrincipal;
      this.fechaInicio = fechaInicio;
      this.fechaFin = fechaFin;
      this.cuposExtraContratados = Math.max(0, cuposExtraContratados);
      this.perfiles = perfiles != null ? new ArrayList<>(perfiles) : new ArrayList<>();
   }

   /**
    * Constructor simplificado - para creación nueva sin perfiles
    */
   public Cuenta(Long id, Servicio servicio, String clave, String correoPrincipal, LocalDate fechaInicio, LocalDate fechaFin) {

      this(id, servicio, clave, correoPrincipal, fechaInicio, fechaFin, 0, new ArrayList<>());
   }



   // ======================================================
   // =============== GETTERS CON PROTECCIÓN ===============
   // ======================================================

   /**
    * Retorna copia inmutable de los perfiles
    */
   public List<PerfilCuenta> getPerfiles() {
      return Collections.unmodifiableList(perfiles);
   }

   /**
    * Retorna copia mutable solo para uso interno (repositorios)
    */
   public List<PerfilCuenta> getPerfilesMutable() {
      return perfiles;
   }

   // ======================================================
   // =============== SETTERS PROTEGIDOS ===================
   // ======================================================

   /**
    * ⚠️ Solo para uso de mappers de persistencia
    */
   public void setCuposExtraContratados(int cupos) {
      if (cupos < 0) {
         throw new IllegalArgumentException("Cupos extra no puede ser negativo");
      }
      if (cupos > servicio.getMaxPerfilesExtra()) {
         throw new IllegalArgumentException(String.format("Cupos extra (%d) excede el máximo permitido (%d)", cupos, servicio.getMaxPerfilesExtra()));
      }
      this.cuposExtraContratados = cupos;
   }

   // ======================================================
   // =============== VALIDACIONES =========================
   // ======================================================

   private static void validarParametrosBasicos(Servicio servicio, String nombre, String correoPrincipal, LocalDate fechaInicio, LocalDate fechaFin) {
      if (servicio == null) {
         throw new IllegalArgumentException("Servicio no puede ser null");
      }

      if (nombre == null || nombre.isBlank()) {
         throw new IllegalArgumentException("Nombre no puede estar vacío");
      }

      if (correoPrincipal == null || correoPrincipal.isBlank()) {
         throw new IllegalArgumentException("Correo principal no puede estar vacío");
      }

      if (fechaInicio == null) {
         throw new IllegalArgumentException("Fecha inicio no puede ser null");
      }

      if (fechaFin == null) {
         throw new IllegalArgumentException("Fecha fin no puede ser null");
      }

      if (fechaFin.isBefore(fechaInicio)) {
         throw new IllegalArgumentException(String.format("La fecha fin (%s) no puede ser anterior a la fecha inicio (%s)", fechaFin, fechaInicio));
      }
   }
}
