package com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table("cuentas")
public class CuentaEntity  {

   @Id

   private Long id;

   private Long servicioId;

   private String clave;

   private String correoPrincipal;

   private LocalDate fechaInicio;

   private LocalDate fechaFin;

   private int cuposExtraContratados;


   public CuentaEntity(Long id, Long servicioId, String clave, String correoPrincipal, LocalDate fechaInicio, LocalDate fechaFin,
         int cuposExtraContratados) {

      this.id = id;
      this.servicioId = servicioId;
      this.clave = clave;
      this.correoPrincipal = correoPrincipal;
      this.fechaInicio = fechaInicio;
      this.fechaFin = fechaFin;
      this.cuposExtraContratados = cuposExtraContratados;

   }



}
