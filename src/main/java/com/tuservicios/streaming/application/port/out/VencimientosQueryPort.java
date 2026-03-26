package com.tuservicios.streaming.application.port.out;

import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

public interface VencimientosQueryPort {

   Flux<VencimientoPerfilRow> findPerfilesActivosConFechaFinIn(List<LocalDate> fechas);

   record VencimientoPerfilRow(
         Long perfilId,
         LocalDate fechaFin,
         String telefono,
         String clienteNombre,
         String servicioNombre
   ) {}
}
