package com.tuservicios.streaming.application.port.out;

import com.tuservicios.streaming.domain.model.enums.TipoNotificacionVencimiento;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface NotificacionLogPort {

   Mono<Boolean> tryCreate(Long perfilId,
         TipoNotificacionVencimiento tipo,
         String canal,
         Instant sentAt);

   Mono<Void> setProviderMessageId(Long perfilId,
         TipoNotificacionVencimiento tipo,
         String canal,
         String providerMessageId);
}