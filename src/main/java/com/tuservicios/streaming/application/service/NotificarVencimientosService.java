package com.tuservicios.streaming.application.service;

import com.tuservicios.streaming.application.port.in.NotificarVencimientosUseCase;
import com.tuservicios.streaming.application.port.out.NotificacionLogPort;
import com.tuservicios.streaming.application.port.out.NotificacionPort;
import com.tuservicios.streaming.application.port.out.VencimientosQueryPort;
import com.tuservicios.streaming.application.port.out.dto.Canal;
import com.tuservicios.streaming.application.port.out.dto.NotificacionRequest;
import com.tuservicios.streaming.application.port.out.dto.Plantilla;
import com.tuservicios.streaming.domain.model.enums.TipoNotificacionVencimiento;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificarVencimientosService implements NotificarVencimientosUseCase {

   private static final String CANAL_WHATSAPP = "WHATSAPP";

   private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

   private final VencimientosQueryPort vencimientosQueryPort;

   private final NotificacionPort notificacionPort;

   private final NotificacionLogPort notificacionLogPort;

   private final Clock clock;

   @Value("${whatsapp.template-name:aviso_vencimiento}")
   private String templateName;

   @Override
   public Mono<Void> ejecutar() {
      // 1. Obtener la fecha actual base
      LocalDate hoy = LocalDate.now(clock);

      // 2. Definir los días en los que buscaremos vencimientos: hoy (0 días) y pasado
      // mañana (2 días)
      List<LocalDate> fechasObjetivo = List.of(hoy, hoy.plusDays(2));

      return vencimientosQueryPort.findPerfilesActivosConFechaFinIn(fechasObjetivo).flatMap(row -> {
         // 3. Calculamos exactamente cuántos días faltan desde hoy hasta la fecha de fin
         long dias = ChronoUnit.DAYS.between(hoy, row.fechaFin());

         // 4. Mapear la cantidad de días al tipo (2 días antes, o vencido hoy)
         TipoNotificacionVencimiento tipo = mapTipo(dias);
         if (tipo == null) {
            return Mono.empty();
         }

         // 5. Dejar el número de teléfono con solo dígitos para la API de WhatsApp
         String telefono = normalizarTelefono(row.telefono());
         if (telefono.isBlank()) {
            return Mono.empty();
         }

         // 6. Construir el objeto NotificacionRequest con sus 3 parámetros para la
         // plantilla
         NotificacionRequest request = construirRequest(telefono, tipo, row.clienteNombre(), row.servicioNombre(),
               row.fechaFin());

         // 7. Intentar crear un log en base de datos para no enviar mensajes duplicados
         return notificacionLogPort.tryCreate(row.perfilId(), tipo, CANAL_WHATSAPP, Instant.now(clock))
               .flatMap(created -> {
                  if (!created) {
                     return Mono.empty();
                  }

                  return notificacionPort
                        .enviar(request)
                        .flatMap(providerMessageId -> notificacionLogPort.setProviderMessageId(row.perfilId(), tipo,
                              CANAL_WHATSAPP, providerMessageId));
               });
      }, 8).then();
   }

   private TipoNotificacionVencimiento mapTipo(long dias) {
      if (dias == 2) {
         return TipoNotificacionVencimiento.TWO_DAYS_BEFORE;
      }
      if (dias == 0) {
         return TipoNotificacionVencimiento.EXPIRED_TODAY;
      }
      return null;
   }

   private NotificacionRequest construirRequest(String telefono, TipoNotificacionVencimiento tipo, String clienteNombre,
         String nombreServicio, LocalDate fechaFin) {

      // Parámetro {{3}} en la plantilla de Meta (Fecha exacta del vencimiento)
      String fechaTexto = fechaFin.format(FORMATO_FECHA);

      // Parámetro {{2}} en la plantilla de Meta (El estado del servicio + la
      // instrucción de Nequi)
      String estadoTexto = switch (tipo) {
         case FIVE_DAYS_BEFORE -> "se vence en 5 días"; // Fallback por si acaso
         case TWO_DAYS_BEFORE -> "se vence en 2 días. Si deseas continuar con el perfil, envía tu pago al Nequi";
         case EXPIRED_TODAY -> "se venció hoy. Si deseas continuar con el perfil, envía tu pago al Nequi";
      };

      // Se usa la plantilla de properties (por defecto: streaming_notif)
      Plantilla plantilla = new Plantilla(templateName, "es");

      // El constructor de request armará el JSON de Meta con la lista:
      // {{1}}=nombreServicio, {{2}}=estadoTexto, {{3}}=fechaTexto
      return new NotificacionRequest(telefono, Canal.WHATSAPP, plantilla,
            List.of(clienteNombre, nombreServicio, estadoTexto, fechaTexto));
   }

   private String normalizarTelefono(String telefono) {
      if (telefono == null) {
         return "";
      }
      return telefono.replaceAll("[^0-9]", "");
   }
}