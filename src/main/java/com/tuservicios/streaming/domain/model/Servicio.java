package com.tuservicios.streaming.domain.model;


import lombok.Getter;
import lombok.Setter;
@Getter
public class Servicio {

   @Setter
   private Long id;

   private final String nombreServicio;
   private final int maxPerfilesBase;
   private final int maxPerfilesExtra;
   private final double valorBase;
   private final double valorPerfil;
   private final double costoPerfilExtra;
   private final double valorPerfilExtra;

   private Servicio(Long id, String nombreServicio, int maxPerfilesBase, int maxPerfilesExtra,
         double valorBase, double valorPerfil, double costoPerfilExtra, double valorPerfilExtra) {

      validar(nombreServicio, maxPerfilesBase, maxPerfilesExtra, valorBase, valorPerfil, costoPerfilExtra, valorPerfilExtra);

      // ✅ id puede ser null SOLO en creación
      this.id = id;
      this.nombreServicio = nombreServicio;
      this.maxPerfilesBase = maxPerfilesBase;
      this.maxPerfilesExtra = maxPerfilesExtra;
      this.valorBase = valorBase;
      this.valorPerfil = valorPerfil;
      this.costoPerfilExtra = costoPerfilExtra;
      this.valorPerfilExtra = valorPerfilExtra;
   }

   // ✅ para insertar (id null, DB lo genera)
   public static Servicio crearNuevo(String nombreServicio, int maxPerfilesBase, int maxPerfilesExtra,
         double valorTotalCuenta, double valorPerfil, double costoPerfilExtra, double valorPerfilExtra) {
      return new Servicio(null, nombreServicio, maxPerfilesBase, maxPerfilesExtra, valorTotalCuenta, valorPerfil, costoPerfilExtra, valorPerfilExtra);
   }

   // ✅ para reconstruir desde BD (id obligatorio)
   public static Servicio reconstruir(Long id, String nombreServicio, int maxPerfilesBase, int maxPerfilesExtra,
         double valorBase, double valorPerfil, double costoPerfilExtra, double valorPerfilExtra) {
      if (id == null) throw new IllegalArgumentException("ID no puede ser null");
      return new Servicio(id, nombreServicio, maxPerfilesBase, maxPerfilesExtra, valorBase, valorPerfil, costoPerfilExtra, valorPerfilExtra);
   }

   public int totalCuposPermitidos(int extras) {
      if (extras < 0) throw new IllegalArgumentException("Extras no puede ser negativo");
      return maxPerfilesBase + extras;
   }

   private static void validar(String nombreServicio, int maxPerfilesBase, int maxPerfilesExtra,
         double valorBase, double valorPerfil, double costoPerfilExtra, double valorPerfilExtra) {
      if (nombreServicio == null || nombreServicio.isBlank()) throw new IllegalArgumentException("Nombre no puede ser vacío");
      if (maxPerfilesBase < 0) throw new IllegalArgumentException("MaxPerfilesBase no puede ser negativo");
      if (maxPerfilesExtra < 0) throw new IllegalArgumentException("MaxPerfilesExtra no puede ser negativo");
      if (valorBase < 0) throw new IllegalArgumentException("ValorBase no puede ser negativo");
      if (valorPerfil < 0) throw new IllegalArgumentException("ValorPerfil no puede ser negativo");
      if (costoPerfilExtra < 0) throw new IllegalArgumentException("CostoPerfilExtra no puede ser negativo");
      if (valorPerfilExtra < 0) throw new IllegalArgumentException("ValorPerfilExtra no puede ser negativo");
   }
}