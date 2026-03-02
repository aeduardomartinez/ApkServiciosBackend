package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDate;

public record PerfilResponse(long id, String nombreCliente, String telefono, LocalDate fechaInicio, LocalDate fechaFin, String estado) {

}
