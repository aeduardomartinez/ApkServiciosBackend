package com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanzasResumenResponse {
    private double ingresoTotalMensual;
    private double costoTotalMensual;
    private double gananciaNetaMensual;
    private int totalCuentas;
    private int totalPerfilesActivos;
    private int totalPerfilesLibres;
}
