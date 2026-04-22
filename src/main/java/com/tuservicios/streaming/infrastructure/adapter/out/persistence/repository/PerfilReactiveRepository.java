package com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository;

import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.PerfilEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PerfilReactiveRepository extends R2dbcRepository<PerfilEntity, Long> {

   Flux<PerfilEntity> findByCuentaId(Long cuentaId);

   /**
    * Trae los perfiles de una cuenta calculando el estado en tiempo real:
    * - LIBRE  → sin cliente asignado
    * - VENCIDO → fecha_fin < hoy
    * - ACTIVO  → tiene cliente y no ha vencido
    */
   @Query("""
         SELECT id_perfil, cuenta_id,
                cliente_id, nombre_cliente, telefono_cliente,
                fecha_inicio, fecha_fin,
                CASE
                  WHEN nombre_cliente IS NULL OR nombre_cliente = '' THEN 'LIBRE'
                  WHEN fecha_fin < (now() AT TIME ZONE 'America/Bogota')::date THEN 'VENCIDO'
                  ELSE 'ACTIVO'
                END AS estado
         FROM perfiles
         WHERE cuenta_id = :cuentaId
         ORDER BY id_perfil ASC
         """)
   Flux<PerfilEntity> findByCuentaIdConEstadoDinamico(Long cuentaId);

   /**
    * Igual pero para todas las cuentas (cuando no se filtra por servicio).
    */
   @Query("""
         SELECT id_perfil, cuenta_id,
                cliente_id, nombre_cliente, telefono_cliente,
                fecha_inicio, fecha_fin,
                CASE
                  WHEN nombre_cliente IS NULL OR nombre_cliente = '' THEN 'LIBRE'
                  WHEN fecha_fin < (now() AT TIME ZONE 'America/Bogota')::date THEN 'VENCIDO'
                  ELSE 'ACTIVO'
                END AS estado
         FROM perfiles
         ORDER BY id_perfil ASC
         """)
   Flux<PerfilEntity> findAllConEstadoDinamico();

   // Buscar primer perfil libre
   Mono<PerfilEntity> findFirstByCuentaIdAndEstadoOrderByIdPerfilAsc(Long cuentaId, String estado);

   @Query("""
         SELECT * FROM perfiles 
         WHERE LOWER(nombre_cliente) LIKE LOWER(CONCAT('%', :nombre, '%'))
         AND nombre_cliente IS NOT NULL
         ORDER BY nombre_cliente ASC
         """)
   Flux<PerfilEntity> findByNombreClienteContaining(String nombre);

   @Query("SELECT COUNT(*) FROM perfiles WHERE cuenta_id = :cuentaId")
   Mono<Long> countByCuentaId(Long cuentaId);

   // Update "liberar"
   @Query("""
         UPDATE perfiles
         SET nombre_cliente = NULL,
             telefono_cliente = NULL,
             fecha_inicio = NULL,
             fecha_fin = NULL,
             estado = 'LIBRE'
         WHERE cuenta_id = :cuentaId
           AND id_perfil = :perfilId
         """)
   Mono<Integer> liberarPerfil(Long cuentaId, Long perfilId);

   @Query("""
    UPDATE perfiles
    SET cliente_id       = :clienteId,
        nombre_cliente   = :nombre,
        telefono_cliente = :telefono,
        fecha_inicio     = :fechaInicio,
        fecha_fin        = :fechaFin,
        estado           = CASE WHEN :fechaFin < (now() AT TIME ZONE 'America/Bogota')::date THEN 'VENCIDO' ELSE 'ACTIVO' END
    WHERE cuenta_id  = :cuentaId
      AND id_perfil  = :perfilId
      AND estado     = 'LIBRE'
    """)
   Mono<Integer> asignarEnPerfil(Long cuentaId, Long perfilId,
         Long clienteId, String nombre, String telefono,
         LocalDate fechaInicio, LocalDate fechaFin);

   @Query("""
    UPDATE perfiles
    SET cliente_id       = :clienteId,
        nombre_cliente   = :nombre,
        telefono_cliente = :telefono,
        fecha_inicio     = :fechaInicio,
        fecha_fin        = :fechaFin,
        estado           = CASE WHEN :fechaFin < (now() AT TIME ZONE 'America/Bogota')::date THEN 'VENCIDO' ELSE 'ACTIVO' END
    WHERE cuenta_id = :cuentaId
      AND id_perfil = :perfilId
    """)
   Mono<Integer> actualizarCliente(Long cuentaId, Long perfilId,
         Long clienteId, String nombre, String telefono,
         LocalDate fechaInicio, LocalDate fechaFin);

   @Query("INSERT INTO perfiles (cuenta_id, estado) VALUES (:cuentaId, 'LIBRE')")
   Mono<Integer> insertarLibre(Long cuentaId);

   Mono<Boolean> existsByCuentaId(Long cuentaId);

}
