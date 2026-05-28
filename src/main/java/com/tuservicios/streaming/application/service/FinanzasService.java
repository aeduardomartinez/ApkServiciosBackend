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
                .reduce(
                        new FinanzasResumenResponse(0, 0, 0, 0, 0, 0),
                        (acc, cuenta) -> {
                            double totalCostos = acc.getCostoTotalMensual() + cuenta.getServicio().getValorBase() 
                                               + (cuenta.getCuposExtraContratados() * cuenta.getServicio().getCostoPerfilExtra());
                            
                            double totalIngresos = acc.getIngresoTotalMensual();
                            int perfilesActivos = acc.getTotalPerfilesActivos();
                            int perfilesLibres = acc.getTotalPerfilesLibres();

                            int maxPerfilesBase = cuenta.getServicio().getMaxPerfilesBase();
                            double valorPerfilBase = cuenta.getServicio().getValorPerfil();
                            double valorPerfilExtra = cuenta.getServicio().getValorPerfilExtra();

                            var perfiles = cuenta.getPerfiles();
                            for (int i = 0; i < perfiles.size(); i++) {
                                var p = perfiles.get(i);
                                if (p.getEstado() == EstadoPerfil.ACTIVO) {
                                    perfilesActivos++;
                                    if (i < maxPerfilesBase) {
                                        totalIngresos += valorPerfilBase;
                                    } else {
                                        totalIngresos += valorPerfilExtra;
                                    }
                                } else if (p.getEstado() == EstadoPerfil.LIBRE) {
                                    perfilesLibres++;
                                }
                            }

                            acc.setIngresoTotalMensual(totalIngresos);
                            acc.setCostoTotalMensual(totalCostos);
                            acc.setGananciaNetaMensual(totalIngresos - totalCostos);
                            acc.setTotalCuentas(acc.getTotalCuentas() + 1);
                            acc.setTotalPerfilesActivos(perfilesActivos);
                            acc.setTotalPerfilesLibres(perfilesLibres);

                            return acc;
                        }
                )
                .defaultIfEmpty(new FinanzasResumenResponse(0, 0, 0, 0, 0, 0));
    }
}
