package com.tuservicios.streaming.application.service;

import org.springframework.stereotype.Service;

import com.tuservicios.streaming.application.port.in.FinanzasUseCase;
import com.tuservicios.streaming.application.port.out.CuentaRepositoryPort;
import com.tuservicios.streaming.domain.model.Cuenta;
import com.tuservicios.streaming.domain.model.enums.EstadoPerfil;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.FinanzasResumenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FinanzasService implements FinanzasUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Mono<FinanzasResumenResponse> calcularFinanzasGlobales() {
        return cuentaRepositoryPort.findAll()
                .collectList()
                .map(cuentas -> {
                    double totalIngresos = 0;
                    double totalCostos = 0;
                    int cuentasActivas = 0;
                    int perfilesActivos = 0;
                    int perfilesLibres = 0;

                    for (Cuenta cuenta : cuentas) {
                        cuentasActivas++;
                        // El costo de la cuenta es su valor base
                        totalCostos += cuenta.getServicio().getValorBase();

                        // Contar los perfiles
                        long activosEnCuenta = cuenta.getPerfiles().stream()
                                .filter(p -> p.getEstado() == EstadoPerfil.ACTIVO)
                                .count();
                        
                        long libresEnCuenta = cuenta.getPerfiles().stream()
                                .filter(p -> p.getEstado() == EstadoPerfil.LIBRE)
                                .count();

                        perfilesActivos += activosEnCuenta;
                        perfilesLibres += libresEnCuenta;

                        // Los ingresos de esta cuenta son (activos * valor_perfil)
                        totalIngresos += (activosEnCuenta * cuenta.getServicio().getValorPerfil());
                    }

                    return FinanzasResumenResponse.builder()
                            .ingresoTotalMensual(totalIngresos)
                            .costoTotalMensual(totalCostos)
                            .gananciaNetaMensual(totalIngresos - totalCostos)
                            .totalCuentas(cuentasActivas)
                            .totalPerfilesActivos(perfilesActivos)
                            .totalPerfilesLibres(perfilesLibres)
                            .build();
                });
    }
}
