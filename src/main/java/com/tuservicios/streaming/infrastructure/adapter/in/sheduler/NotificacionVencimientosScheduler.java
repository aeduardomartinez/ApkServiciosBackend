package com.tuservicios.streaming.infrastructure.adapter.in.sheduler;

import com.tuservicios.streaming.application.port.in.NotificarVencimientosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionVencimientosScheduler {

   private final NotificarVencimientosUseCase useCase;

   @Scheduled(cron = "0 0 8 * * *")
   public void ejecutarDiario() {
      useCase.ejecutar()
             .subscribe();
   }
}