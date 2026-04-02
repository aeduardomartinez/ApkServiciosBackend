package com.tuservicios.streaming.application.service;

import com.tuservicios.streaming.application.port.in.NotificarVencimientosUseCase;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificarVencimientosService implements NotificarVencimientosUseCase {

   private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

   private final VencimientosQueryPort vencimientosQueryPort;

   private final NotificacionPort notificacionPort;

   private final Clock clock;

   @Value("${whatsapp.template-name:aviso_vencimiento}")
   private String templateName;

   @Override
   public Mono<Void> ejecutar() {
      // 1. Obtener la fecha actual base
      LocalDate hoy = LocalDate.now(clock);

      // 2. Definir los días en los que buscaremos vencimientos
      List<LocalDate> fechasObjetivo = List.of(
            hoy.minusDays(1), // Ayer (por si se pasó)
            hoy,              // Hoy
            hoy.plusDays(2),  // Mañana pasado
            hoy.plusDays(5)   // 5 días
      );

      return vencimientosQueryPort.findPerfilesActivosConFechaFinIn(fechasObjetivo).flatMap(row -> {
         // 3. Calculamos exactamente cuántos días faltan
         long dias = ChronoUnit.DAYS.between(hoy, row.fechaFin());
         TipoNotificacionVencimiento tipo = mapTipo(dias);
         if (tipo == null) return Mono.empty();

         String telefono = normalizarTelefono(row.telefono());
         if (telefono.isBlank()) return Mono.empty();

         // 4. Construir request diferenciado por ID de perfil para evitar bloqueos por duplicado
         NotificacionRequest request = construirRequest(row.perfilId(), telefono, tipo, row.clienteNombre(), row.servicioNombre(),
               row.fechaFin());

         // 5. Enviar mensaje directamente (sin logs/bloqueos)
         log.info("Auto-envío recordatorio perfilId={}, cliente={}, servicio={}", 
               row.perfilId(), row.clienteNombre(), row.servicioNombre());
         return notificacionPort.enviar(request).then();
      }, 8).then();
   }

   @Override
   public Mono<Void> ejecutarPorPerfil(Long perfilId) {
      LocalDate hoy = LocalDate.now(clock);

      return vencimientosQueryPort.findPerfilPorId(perfilId).flatMap(row -> {
         long dias = ChronoUnit.DAYS.between(hoy, row.fechaFin());
         TipoNotificacionVencimiento tipo = (dias <= 0) ? TipoNotificacionVencimiento.EXPIRED_TODAY : TipoNotificacionVencimiento.TWO_DAYS_BEFORE;

         String telefono = normalizarTelefono(row.telefono());
         if (telefono.isBlank()) return Mono.empty();

         NotificacionRequest request = construirRequest(row.perfilId(), telefono, tipo, row.clienteNombre(), row.servicioNombre(),
               row.fechaFin());

         log.info("Manual-envío recordatorio perfilId={}, cliente={}, servicio={}", 
               perfilId, row.clienteNombre(), row.servicioNombre());

         return notificacionPort.enviar(request).then();
      }).switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el perfil con ID: " + perfilId)));
   }

   private TipoNotificacionVencimiento mapTipo(long dias) {
      if (dias == 5) return TipoNotificacionVencimiento.FIVE_DAYS_BEFORE;
      if (dias == 2) return TipoNotificacionVencimiento.TWO_DAYS_BEFORE;
      if (dias <= 0) return TipoNotificacionVencimiento.EXPIRED_TODAY;
      return null;
   }

   private NotificacionRequest construirRequest(Long perfilId, String telefono, TipoNotificacionVencimiento tipo, String clienteNombre,
         String nombreServicio, LocalDate fechaFin) {

      String fechaTexto = fechaFin.format(FORMATO_FECHA);
      String estadoTexto = (tipo == TipoNotificacionVencimiento.EXPIRED_TODAY) ? "(VENCIDO)" : "";

      // DIFERENCIACIÓN CLAVE: Incluimos el ID de perfil en el nombre del servicio para que el mensaje sea único ante Meta
      String servicioDiferenciado = nombreServicio + " (Ref: " + perfilId + ")";

      Plantilla plantilla = new Plantilla(templateName, "es");

      // {{1}}=NombreCliente, {{2}}=ServicioDiferenciado, {{3}}=EstadoTexto, {{4}}=FechaTexto
      return new NotificacionRequest(telefono, Canal.WHATSAPP, plantilla,
            List.of(clienteNombre, servicioDiferenciado, estadoTexto, fechaTexto));
   }

   private String normalizarTelefono(String telefono) {
      if (telefono == null) return "";
      return telefono.replaceAll("[^0-9]", "");
   }
}