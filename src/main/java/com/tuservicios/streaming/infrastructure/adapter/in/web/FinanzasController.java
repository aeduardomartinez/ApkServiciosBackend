package com.tuservicios.streaming.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuservicios.streaming.application.port.in.FinanzasUseCase;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.FinanzasResumenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finanzas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ajusta según la configuración de tu frontend
public class FinanzasController {

    private final FinanzasUseCase finanzasUseCase;

    @GetMapping("/resumen")
    public Mono<FinanzasResumenResponse> getResumenFinanciero() {
        return finanzasUseCase.calcularFinanzasGlobales();
    }
}
