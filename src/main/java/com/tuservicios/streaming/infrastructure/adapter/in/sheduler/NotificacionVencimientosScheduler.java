package com.tuservicios.streaming.infrastructure.adapter.in.sheduler;

import com.tuservicios.streaming.application.port.in.NotificarVencimientosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionVencimientosScheduler {

   private final NotificarVencimientosUseCase useCase;

   // 12:00 PM hora Colombia (UTC-5) = 17:00 UTC
   @Scheduled(cron = "0 0 17 * * *")
   public void ejecutarDiario() {
      useCase.ejecutar()
             .subscribe();
   }
}