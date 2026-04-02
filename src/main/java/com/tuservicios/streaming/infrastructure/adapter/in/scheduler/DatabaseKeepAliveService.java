package com.tuservicios.streaming.infrastructure.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Servicio encargado de mantener activa la conexión con la base de datos (Neon).
 * Realiza un ping ("SELECT 1") cada 1 minuto para evitar que la capa gratuita entre en reposo.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseKeepAliveService {

    private final DatabaseClient db;

    // Ejecuta cada 1 minuto (60,000 ms)
    @Scheduled(fixedRate = 60000)
    public void keepDatabaseAlive() {
        db.sql("SELECT 1")
          .fetch()
          .one()
          .doOnNext(res -> log.debug("DB Ping (Neon): Exitoso"))
          .doOnError(err -> log.error("DB Ping (Neon): Fallido - {}", err.getMessage()))
          .subscribe();
    }
}
