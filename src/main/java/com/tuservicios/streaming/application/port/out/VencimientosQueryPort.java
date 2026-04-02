package com.tuservicios.streaming.application.port.out;

import reactor.core.publisher.Flux;

import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface VencimientosQueryPort {

   Flux<VencimientoPerfilRow> findPerfilesActivosConFechaFinIn(List<LocalDate> fechas);

   Mono<VencimientoPerfilRow> findPerfilPorId(Long perfilId);

   record VencimientoPerfilRow(
         Long perfilId,
         LocalDate fechaFin,
         String telefono,
         String clienteNombre,
         String servicioNombre
   ) {}
}
