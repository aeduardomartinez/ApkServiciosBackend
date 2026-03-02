package com.tuservicios.streaming.application.service;

import static reactor.core.publisher.Operators.as;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.tuservicios.streaming.application.port.in.CuentaUseCase;
import com.tuservicios.streaming.application.port.out.ClienteRegistradoRepositoryPort;
import com.tuservicios.streaming.application.port.out.CuentaRepositoryPort;
import com.tuservicios.streaming.application.port.out.ServicioRepositoryPort;
import com.tuservicios.streaming.domain.exception.ClienteNoEncontradoException;
import com.tuservicios.streaming.domain.exception.CuentaDuplicadaException;
import com.tuservicios.streaming.domain.exception.CuentaNoEncontradaException;
import com.tuservicios.streaming.domain.exception.ServicioNotFoundException;
import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.domain.model.Cuenta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaService implements CuentaUseCase {

   private final CuentaRepositoryPort cuentaRepo;

   private final ServicioRepositoryPort servicioRepo;

   private final TransactionalOperator tx;

   private final ClienteRegistradoRepositoryPort clienteRegistradoRepo;

   private <T> Mono<T> inTx(Mono<T> mono) {
      return mono.as(tx::transactional);
   }

   private <T> Flux<T> inTx(Flux<T> flux) {
      return flux.as(tx::transactional);
   }
   // ======================================================
   // =============== CREACIÓN DE CUENTA ===================
   // ======================================================

   /**
    * Crea una nueva cuenta maestra para un servicio.
    * Validaciones:
    * - Servicio debe existir
    * - Correo no debe estar duplicado
    * - Fechas deben ser válidas
    * Postcondición: Cuenta creada con perfiles base en estado LIBRE
    */

   @Override
   public Mono<Cuenta> crearCuenta(Long servicioId, String clave, String correo , LocalDate inicio, LocalDate fin) {

      log.info("Creando cuenta para servicio: {}, correo: {}", servicioId, correo);

      return inTx(servicioRepo.findById(servicioId)
                         .switchIfEmpty(Mono.error(new ServicioNotFoundException("Servicio con ID " + servicioId + " no encontrado")))
                         .flatMap(servicio -> {

                            // 1) Crear agregado raíz (SIN perfiles aún; se crearán en BD)
                            Cuenta cuenta = new Cuenta(
                                 null,
                                  servicio,
                                  clave,
                                  correo,
                                  inicio,
                                  fin,
                                  0,
                                  List.of()       // perfiles vacíos en memoria
                            );

                            // 2) Persistir cuenta
                            return cuentaRepo.save(cuenta)

                                             // 3) Crear perfiles base en BD
                                             .flatMap(saved -> {
                                                int cantidad = servicio.totalCuposPermitidos(saved.getCuposExtraContratados());
                                                log.info("Creando perfiles iniciales: cuentaId={}, cantidad={}", saved.getId(), cantidad);

                                                return cuentaRepo.crearPerfilesIniciales(saved.getId(), cantidad)
                                                                 .then(cuentaRepo.findById(saved.getId()));
                                             });
                         })
                         .onErrorResume(this::manejarErrorCreacion));
   }
   /**
    * Maneja errores de creación, especialmente duplicados de correo
    */
   private Mono<Cuenta> manejarErrorCreacion(Throwable error) {
      String mensaje = error.getMessage();


      if (mensaje != null && (mensaje.contains("23505") || mensaje.contains("duplicate key"))) {
         return Mono.error(new CuentaDuplicadaException("Ya existe una cuenta con ese correo electrónico"));
      }

      return Mono.error(error);
   }

   // ======================================================
   // =============== GESTIÓN DE PERFILES ==================
   // ======================================================
   @Override
   public Mono<Cuenta> crearPerfilesIniciales(Long cuentaId) {


      return inTx(cuentaRepo.findById(cuentaId)
                       .switchIfEmpty(Mono.error(new CuentaNoEncontradaException("Cuenta con ID " + cuentaId + " no encontrada")))
                       .flatMap(cuenta -> {

                          // Si ya existen perfiles en BD, no hacer nada
                          if (!cuenta.getPerfiles().isEmpty()) {
                             log.info("La cuenta {} ya tiene perfiles creados", cuentaId);
                             return Mono.just(cuenta);
                          }

                          // Calcular cupos base + extras contratados
                          int cantidad = cuenta.getServicio().totalCuposPermitidos(cuenta.getCuposExtraContratados());

                          // 🔥 INSERT de perfiles LIBRES en BD
                          return cuentaRepo.crearPerfilesIniciales(cuentaId, cantidad)
                                           .then(cuentaRepo.findById(cuentaId));
                       })
      );
   }


   /**
    * Metodo encargado de asignar un cliente a un perfil
    */
   // CuentaService.java — método asociarUsuario actualizado
   @Override
   public Mono<Cuenta> asociarUsuario(Long cuentaId, Long clienteId,
         LocalDate fechaInicio, LocalDate fechaFin) {
      log.info("Asignando clienteId={} a cuentaId={}", clienteId, cuentaId);

      return inTx(
            clienteRegistradoRepo.findById(clienteId)
                                 .switchIfEmpty(Mono.error(
                                       new ClienteNoEncontradoException("Cliente " + clienteId + " no encontrado")))
                                 .flatMap(cr -> {
                                    Cliente cliente = new Cliente(cr.nombreCompleto(), cr.telefono());
                                    return cuentaRepo.asignarClienteEnPerfilLibre(
                                          cuentaId, clienteId, cliente, fechaInicio, fechaFin);
                                 })
                                 .then(cuentaRepo.findById(cuentaId))
      );
   }@Override
   public Mono<Cuenta> editarPerfil(Long cuentaId, Long perfilId,
         Long clienteId,
         LocalDate fechaInicio, LocalDate fechaFin) {
      log.info("Editando perfilId={} en cuentaId={} → nuevo clienteId={}", perfilId, cuentaId, clienteId);

      return inTx(
            clienteRegistradoRepo.findById(clienteId)
                                 .switchIfEmpty(Mono.error(
                                       new ClienteNoEncontradoException("Cliente " + clienteId + " no encontrado")))
                                 .flatMap(cr -> {
                                    Cliente cliente = new Cliente(cr.nombreCompleto(), cr.telefono());
                                    return cuentaRepo.actualizarClienteEnPerfil(
                                          cuentaId, perfilId, clienteId, cliente, fechaInicio, fechaFin);
                                 })
                                 .then(cuentaRepo.findById(cuentaId))
      );
   }
   /**
    * Requerimiento 5: Libera un cupo sin borrar el historial.
    * <p>
    * El cliente se mantiene en el perfil para auditoría,
    * pero el estado cambia a LIBRE para permitir nueva asignación.
    */
   @Override
   public Mono<Cuenta> liberarPerfil(Long cuentaId, Long perfilId) {

      log.info("Liberando perfil {} de cuenta {}", perfilId, cuentaId);



      return inTx(cuentaRepo
            .liberarPerfil(cuentaId, perfilId)
            .then(cuentaRepo.findById(cuentaId)));

   }

   // ======================================================
   // =============== BÚSQUEDA Y CONSULTA ==================
   // ======================================================

   /**
    * Requerimiento 6: Búsqueda global de usuarios y sus estados.
    * <p>
    * Busca cuentas que tengan perfiles con clientes cuyo nombre coincida.
    */
   @Override
   public Flux<Cuenta> buscarPorNombreCliente(String nombre) {

      log.info("Buscando cuentas por nombre de cliente: {}", nombre);

      if (nombre == null || nombre.isBlank()) {
         return Flux.error(new IllegalArgumentException("Nombre de búsqueda no puede estar vacío"));
      }

      return cuentaRepo.findByClienteNombre(nombre.trim());
   }

   /**
    * Obtener una cuenta por ID
    */
   public Mono<Cuenta> obtenerCuenta(Long cuentaId) {
      return cuentaRepo.findById(cuentaId).switchIfEmpty(Mono.error(new CuentaNoEncontradaException("Cuenta con ID " + cuentaId + " no encontrada")));
   }

   /**
    * Listar todas las cuentas
    */
   @Override
   public Flux<Cuenta> listarTodasLasCuentas() {
      return cuentaRepo.findAll();
   }

   @Override
   public Flux<Cuenta> listarCuentas(Long servicio) {

      if (servicio == null || servicio.equals(0)) {
         return cuentaRepo.findAll(); // trae todas con perfiles
      }

      return cuentaRepo.findByServicioId(servicio);
   }


}
