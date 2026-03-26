package com.tuservicios.streaming.infrastructure.adapter.out.persistence;

import com.tuservicios.streaming.application.port.out.VencimientosQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class VencimientosQueryAdapter implements VencimientosQueryPort {

   private final DatabaseClient db;

   @Override
   public Flux<VencimientoPerfilRow> findPerfilesActivosConFechaFinIn(List<LocalDate> fechas) {
      LocalDate[] fechasArray = fechas.toArray(LocalDate[]::new);

      return db.sql("""
                SELECT
                  p.id_perfil AS perfil_id,
                  p.fecha_fin AS fecha_fin,
                  COALESCE(p.telefono_cliente, cte.telefono) AS telefono,
                  COALESCE(p.nombre_cliente, (cte.nombre || ' ' || cte.apellido)) AS cliente_nombre,
                  s.nombre AS servicio_nombre
                FROM perfiles p
                JOIN cuentas c ON c.id = p.cuenta_id
                JOIN servicios s ON s.id = c.servicio_id
                LEFT JOIN clientes cte ON cte.id = p.cliente_id
                WHERE p.estado = 'ACTIVO'
                  AND p.fecha_fin = ANY($1)
                """)
               .bind(0, fechasArray)
               .map((row, meta) -> new VencimientoPerfilRow(
                     row.get("perfil_id", Long.class),
                     row.get("fecha_fin", LocalDate.class),
                     row.get("telefono", String.class),
                     row.get("cliente_nombre", String.class),
                     row.get("servicio_nombre", String.class)
               ))
               .all();
   }
}