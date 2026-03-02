package com.tuservicios.streaming.infrastructure.adapter.out.persistence;

import com.tuservicios.streaming.application.port.out.NotificacionPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component // Indica a Spring que esta es la implementación del puerto de notificaciones
public class WhatsAppAdapter implements NotificacionPort {

   @Override
   public Mono<Void> enviarMensajeWhatsApp(String telefono, String mensaje) {
      // Aquí integrarías con la API de Meta, Twilio o similares
      System.out.println("Enviando WhatsApp a " + telefono + ": " + mensaje);
      return Mono.empty();
   }
}