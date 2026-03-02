package com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("clientes")
public class ClienteRegistradoEntity {

   @Id
   private Long id;

   @Column("nombre")
   private String nombre;

   @Column("apellido")
   private String apellido;

   @Column("telefono")
   private String telefono;
}