package com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table("servicios")
public class ServiciosEntity  {

   @Id @Column("id")
   Long id;
   @Column("nombre")
   String nombre;

   @Column("max_perfiles_base")
   Integer maxPerfilesBase;

   @Column("max_perfiles_extra")
   Integer maxPerfilesExtra;

   @Column("valor_base")
   BigDecimal valorBase;

   BigDecimal valorPerfil;



   public ServiciosEntity(Long id, String nombre, Integer maxPerfilesBase, Integer maxPerfilesExtra, BigDecimal valorBase, BigDecimal valorPerfil
          ) {
      this.id = id;
      this.nombre = nombre;
      this.maxPerfilesBase = maxPerfilesBase;
      this.maxPerfilesExtra = maxPerfilesExtra;
      this.valorBase = valorBase;
      this.valorPerfil = valorPerfil;
   }

   /**
    * Factory para INSERT (marca isNew=true)
    */
   public static ServiciosEntity newEntity(Long id,
         String nombre,
         Integer maxPerfilesBase,
         Integer maxPerfilesExtra,
         BigDecimal valorBase,
         BigDecimal valorPerfil) {
      return new ServiciosEntity(id, nombre, maxPerfilesBase, maxPerfilesExtra, valorBase, valorPerfil);
   }

}




