package com.tuservicios.streaming.infrastructure.adapter.in.sheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SelfPingScheduler {

   private final WebClient webClient;

   public SelfPingScheduler(@Value("${app.self-ping-url:}") String selfPingUrl) {
      this.webClient = selfPingUrl.isBlank()
            ? null
            : WebClient.builder().baseUrl(selfPingUrl).build();
   }

   /**
    * Se ejecuta cada 10 minutos para mantener la aplicación activa en Render.
    * Render apaga las instancias gratuitas después de 15 min de inactividad.
    */
   @Scheduled(fixedRate = 600_000) // cada 10 minutos (600,000 ms)
   public void selfPing() {
      if (webClient == null) {
         log.debug("Self-ping desactivado: no se configuró 'app.self-ping-url'");
         return;
      }

      webClient.get()
            .uri("/api/ping")
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(body -> log.info("Self-ping exitoso: {}", body))
            .doOnError(err -> log.warn("Self-ping falló: {}", err.getMessage()))
            .subscribe();
   }
}
