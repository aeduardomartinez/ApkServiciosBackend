package com.tuservicios.streaming.infrastructure.adapter.out.persistence;

import com.tuservicios.streaming.application.port.out.NotificacionLogPort;
import com.tuservicios.streaming.domain.model.enums.TipoNotificacionVencimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class NotificacionLogRepositoryAdapter implements NotificacionLogPort {

   private final DatabaseClient db;

   @Override
   public Mono<Boolean> tryCreate(Long perfilId,
         TipoNotificacionVencimiento tipo,
         String canal,
         Instant sentAt) {

      // Postgres: idempotencia nativa (sin excepciones)
      return db.sql("""
                INSERT INTO service_notification_log(perfil_id, notification_type, channel, sent_at)
                VALUES ($1, $2, $3, $4)
                ON CONFLICT (perfil_id, notification_type, channel) DO NOTHING
                """)
               .bind(0, perfilId)
               .bind(1, tipo.name())
               .bind(2, canal)
               .bind(3, sentAt)
               .fetch()
               .rowsUpdated()
               .map(rows -> rows > 0);
   }

   @Override
   public Mono<Void> setProviderMessageId(Long perfilId,
         TipoNotificacionVencimiento tipo,
         String canal,
         String providerMessageId) {

      return db.sql("""
                UPDATE service_notification_log
                SET provider_message_id = $1
                WHERE perfil_id = $2
                  AND notification_type = $3
                  AND channel = $4
                """)
               .bind(0, providerMessageId)
               .bind(1, perfilId)
               .bind(2, tipo.name())
               .bind(3, canal)
               .fetch()
               .rowsUpdated()
               .then();
   }
}