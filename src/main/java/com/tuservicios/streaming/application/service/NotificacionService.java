package com.tuservicios.streaming.application.service;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tuservicios.streaming.application.port.out.CuentaRepositoryPort;
import com.tuservicios.streaming.application.port.out.NotificacionPort;
import com.tuservicios.streaming.domain.model.Cuenta;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificacionService {

   private final CuentaRepositoryPort cuentaRepository;
   private final NotificacionPort whatsappPort;

   // Se ejecuta todos los días a las 8:00 AM
   @Scheduled(cron = "0 0 8 * * *")
   public void procesoNotificacionesDiarias() {
      cuentaRepository.findAll()
                      .flatMapIterable(Cuenta::getPerfiles)
                      .filter(perfil ->perfil.getCliente() != null)
                      .flatMap(perfil ->  {
                         LocalDate hoy = LocalDate.now();

                         // REQ 8: Notificación preventiva (2 días antes)
//                         if (perfil.getFechaFin().minusDays(2).isEqual(hoy)) {
//                            String msg = "Tu cuenta se vencerá en 2 días. ¿Deseas renovar?";
//                            return whatsappPort.enviarMensajeWhatsApp(perfil.getCliente().telefono(), msg);
//                         }

//                         // REQ 7: Notificación de vencimiento hoy
//                         if (perfil.getFechaFin().isEqual(hoy) || perfil.verificarVencimiento()) {
//                            String msg = "Tu perfil ha vencido. Valor renovación: " + perfil.getValorPerfil();
//                            return whatsappPort.enviarMensajeWhatsApp(perfil.getCliente().telefono(), msg);
//                         }

                         return Mono.empty();
                      })
                      .subscribe(); // Importante: en WebFlux, si no hay subscribe() o return, no se ejecuta
   }
}