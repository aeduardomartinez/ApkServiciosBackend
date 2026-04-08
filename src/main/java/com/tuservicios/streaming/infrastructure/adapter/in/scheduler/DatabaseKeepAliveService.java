package com.tuservicios.streaming.infrastructure.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Mantiene vivos tanto la base de datos (Neon) como el servidor en Render.
 *
 * - DB Ping   → SELECT 1 cada 1 min  → evita que Neon duerma
 * - HTTP Ping → GET /ping cada 10 min → evita que Render duerma y mate el @Scheduled
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseKeepAliveService {

    private final DatabaseClient db;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.self-ping-url:https://apkserviciosbackend.onrender.com}")
    private String selfPingUrl;

    /** Ping a Neon DB cada 1 minuto para evitar que la BD entre en reposo. */
    @Scheduled(fixedRate = 60_000)
    public void keepDatabaseAlive() {
        db.sql("SELECT 1")
          .fetch()
          .one()
          .doOnNext(res -> log.debug("[KeepAlive] DB Ping (Neon): OK"))
          .doOnError(err -> log.error("[KeepAlive] DB Ping (Neon): FAILED - {}", err.getMessage()))
          .subscribe();
    }

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
