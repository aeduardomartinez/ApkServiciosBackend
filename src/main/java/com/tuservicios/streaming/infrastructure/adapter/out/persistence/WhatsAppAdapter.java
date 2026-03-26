package com.tuservicios.streaming.infrastructure.adapter.out.persistence;
import com.tuservicios.streaming.application.port.out.NotificacionPort;
import com.tuservicios.streaming.application.port.out.dto.Canal;
import com.tuservicios.streaming.application.port.out.dto.NotificacionRequest;
import com.tuservicios.streaming.infrastructure.config.WhatsappCloudProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatusCode;

@Component
@RequiredArgsConstructor
public class WhatsAppAdapter implements NotificacionPort {

   private final WebClient whatsappWebClient;
   private final WhatsappCloudProperties props;

   @Override
   public Mono<String> enviar(NotificacionRequest request) {

      if (request == null) {
         return Mono.error(new IllegalArgumentException("La notificación no puede ser null"));
      }

      if (request.canal() != Canal.WHATSAPP) {
         return Mono.error(new IllegalArgumentException("Canal no soportado: " + request.canal()));
      }

      String to = normalizeE164DigitsOnly(request.telefonoE164());

      if (to.isBlank()) {
         return Mono.error(new IllegalArgumentException("Teléfono inválido para WhatsApp"));
      }

      if (request.plantilla() == null) {
         return Mono.error(new IllegalArgumentException("La plantilla de WhatsApp es obligatoria"));
      }

      Object payload = buildTemplatePayload(
            to,
            request.plantilla().nombre(),
            request.plantilla().languageCode(),
            request.parametros()
      );

      String url = "/" + props.apiVersion() + "/" + props.phoneNumberId() + "/messages";

      return whatsappWebClient.post()
                              .uri(url)
                              .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.accessToken())
                              .contentType(MediaType.APPLICATION_JSON)
                              .accept(MediaType.APPLICATION_JSON)
                              .bodyValue(buildTemplatePayload(
                                    to,
                                    request.plantilla().nombre(),
                                    request.plantilla().languageCode(),
                                    request.parametros()
                              ))
                              .retrieve()
                              .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalStateException("Error de Meta API: " + body))))
                              .bodyToMono(WhatsappSendResponse.class)
                              .map(this::extractMessageIdOrThrow);
   }

   private Object buildTemplatePayload(String to,
         String templateName,
         String languageCode,
         List<String> parametros) {

      List<Parameter> bodyParameters = parametros == null
            ? List.of()
            : parametros.stream()
                        .map(value -> new Parameter("text", value))
                        .toList();

      return new WhatsappTemplateMessageRequest(
            "whatsapp",
            to,
            "template",
            new Template(
                  templateName,
                  new Language(Objects.requireNonNullElse(languageCode, "es")),
                  List.of(new TemplateComponent("body", bodyParameters))
            )
      );
   }

   private String extractMessageIdOrThrow(WhatsappSendResponse response) {
      if (response == null || response.messages == null || response.messages.isEmpty()) {
         throw new IllegalStateException("Respuesta WhatsApp inválida: no retorna messages.id");
      }

      String id = response.messages.get(0).id;
      if (id == null || id.isBlank()) {
         throw new IllegalStateException("Respuesta WhatsApp inválida: messages.id vacío");
      }

      return id;
   }

   private String normalizeE164DigitsOnly(String telefono) {
      if (telefono == null) {
         return "";
      }
      return telefono.replaceAll("[^0-9]", "");
   }

   private static final class WhatsappTemplateMessageRequest {
      public final String messaging_product;
      public final String to;
      public final String type;
      public final Template template;

      private WhatsappTemplateMessageRequest(String messaging_product, String to, String type, Template template) {
         this.messaging_product = messaging_product;
         this.to = to;
         this.type = type;
         this.template = template;
      }
   }

   private static final class Template {
      public final String name;
      public final Language language;
      public final List<TemplateComponent> components;

      private Template(String name, Language language, List<TemplateComponent> components) {
         this.name = name;
         this.language = language;
         this.components = components;
      }
   }

   private static final class Language {
      public final String code;

      private Language(String code) {
         this.code = code;
      }
   }

   private static final class TemplateComponent {
      public final String type;
      public final List<Parameter> parameters;

      private TemplateComponent(String type, List<Parameter> parameters) {
         this.type = type;
         this.parameters = parameters;
      }
   }

   private static final class Parameter {
      public final String type;
      public final String text;

      private Parameter(String type, String text) {
         this.type = type;
         this.text = text;
      }
   }

   private static final class WhatsappSendResponse {
      public List<MessageId> messages;
   }

   private static final class MessageId {
      public String id;
   }
}