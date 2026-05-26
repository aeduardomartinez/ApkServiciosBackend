package com.tuservicios.streaming.application.port.in;

import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.FinanzasResumenResponse;
import reactor.core.publisher.Mono;

public interface FinanzasUseCase {
    Mono<FinanzasResumenResponse> calcularFinanzasGlobales();
}
