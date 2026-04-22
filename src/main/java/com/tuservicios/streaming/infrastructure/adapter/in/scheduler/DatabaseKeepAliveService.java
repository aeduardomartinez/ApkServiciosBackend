package com.tuservicios.streaming.infrastructure.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Mantiene activo el servidor en Render mediante un ping HTTP periódico.
 * Evita que Render Free Tier duerma el proceso Java, lo que mataría los @Scheduled.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseKeepAliveService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.self-ping-url:https://apkserviciosbackend.onrender.com}")
    private String selfPingUrl;

    /**
     * Ping HTTP al propio servidor en Render cada 10 minutos.
     * Evita que Render Free Tier duerma el proceso Java, lo que mataría el @Scheduled de las 12 PM.
     */
    @Scheduled(fixedRate = 600_000)
    public void keepRenderAlive() {
        webClientBuilder.build()
            .get()
            .uri(selfPingUrl + "/api/ping")
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(res -> log.info("[KeepAlive] Render HTTP Ping: OK (status={})", res.getStatusCode()))
            .doOnError(err -> log.warn("[KeepAlive] Render HTTP Ping: FAILED - {}", err.getMessage()))
            .subscribe();
    }
}
