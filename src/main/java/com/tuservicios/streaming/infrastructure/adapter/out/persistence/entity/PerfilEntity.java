package com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity;


import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("perfiles")
public class PerfilEntity {

   @Id @Column("id_perfil")
   private Long idPerfil;

   @Column("cuenta_id")
   private Long cuentaId;

   @Column("cliente_id")          // ← FK a tabla clientes
   private Long clienteId;

   // Se mantienen para no romper compatibilidad / auditoría
   @Column("nombre_cliente")
   private String nombreCliente;

   @Column("telefono_cliente")
   private String telefonoCliente;

   @Column("fecha_inicio")
   private LocalDate fechaInicio;

   @Column("fecha_fin")
   private LocalDate fechaFin;

   @Column("estado")
   private String estado;
}