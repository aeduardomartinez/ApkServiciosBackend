package com.tuservicios.streaming.infrastructure.adapter.in.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuservicios.streaming.application.port.in.ServiciosUseCase;
import com.tuservicios.streaming.application.service.NotificarVencimientosService;
import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.ServicioRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ServicioListItemResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ServicioResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.mapper.ServicioWebMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

   private final NotificarVencimientosService notificarVencimientosService;

   @Autowired
   public NotificationController(NotificarVencimientosService notificarVencimientosService) {
      this.notificarVencimientosService = notificarVencimientosService;
   }

   @PostMapping("/send-reminder")
   public Mono<ResponseEntity<String>> sendReminder() {
      log.info("REST: Iniciando envío masivo de recordatorios automáticos.");
      return notificarVencimientosService.ejecutar()
            .then(Mono.just(ResponseEntity.ok("Notificaciones masivas procesadas correctamente.")))
            .onErrorResume(err -> {
               log.error("REST: Error inesperado en envío masivo: {}", err.getMessage());
               return Mono.just(ResponseEntity.status(500).body("Error masivo: " + err.getMessage()));
            });
   }

   @PostMapping("/send-reminder/{perfilId}")
   public Mono<ResponseEntity<String>> sendReminderByPerfil(@PathVariable Long perfilId) {
      log.info("REST: Iniciando envío manual para perfilId={}", perfilId);
      return notificarVencimientosService.ejecutarPorPerfil(perfilId)
            .then(Mono.just(ResponseEntity.ok("Recordatorio enviado correctamente al perfil.")))
            .onErrorResume(err -> {
               log.error("REST: Error al enviar recordatorio manual perfilId={}: {}", perfilId, err.getMessage());
               return Mono.just(ResponseEntity.status(400).body("Error: " + err.getMessage()));
            });
   }
}