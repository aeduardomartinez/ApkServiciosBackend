package com.tuservicios.streaming.infrastructure.adapter.in.scheduler;

import com.tuservicios.streaming.application.service.NotificarVencimientosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler que dispara automáticamente el envío de recordatorios
 * de vencimiento por WhatsApp todos los días a las 12:00 PM (hora Colombia).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VencimientoNotificationScheduler {

    private final NotificarVencimientosService notificarVencimientosService;

    /**
     * Se ejecuta todos los días a las 12:00 PM hora Colombia (UTC-5).
     * Cron: segundos minutos horas día mes díaSemana
     */
    // @Scheduled(cron = "0 0 12 * * *", zone = "America/Bogota")
    @Scheduled(cron = "0 */2 * * * *")
    public void enviarRecordatoriosVencimiento() {
        log.info("[Scheduler] ⏰ Iniciando envío automático de recordatorios de vencimiento...");

        notificarVencimientosService.ejecutar()
                .doOnSuccess(v -> log.info("[Scheduler] ✅ Recordatorios enviados correctamente."))
                .doOnError(e -> log.error("[Scheduler] ❌ Error al enviar recordatorios: {}", e.getMessage(), e))
                .subscribe();
    }
}
