package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDate;

public record PerfilResponse(long id, Long clienteId, String nombreCliente, String telefono, LocalDate fechaInicio, LocalDate fechaFin, String estado, boolean isExtra, String correoExtra, String claveExtra) {

}
