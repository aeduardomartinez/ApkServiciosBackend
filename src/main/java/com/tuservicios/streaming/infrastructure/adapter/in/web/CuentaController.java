package com.tuservicios.streaming.infrastructure.adapter.in.web;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tuservicios.streaming.application.port.in.CuentaUseCase;
import com.tuservicios.streaming.domain.model.Cliente;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.CuentaRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.PerfilRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.CuentaResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.mapper.CuentaWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

   private final CuentaUseCase cuentaUseCase;

   private final CuentaWebMapper mapper;

   @PostMapping("/crear")
   @ResponseStatus(HttpStatus.CREATED)
   public Mono<CuentaResponse> crearCuenta(@Valid @RequestBody CuentaRequest request) {

      log.info("REST: Crear cuenta - servicio: {}, correo: {}", request.servicioId(), request.correo());

      return cuentaUseCase
            .crearCuenta(request.servicioId(), request.clave(), request.correo(), request.fechaInicio(), request.fechaFin())
            .map(mapper::toResponse)
            .doOnSuccess(res -> log.info("REST: Cuenta creada id={}", res.id()));
   }

   @GetMapping("/nombre/{nombre}")
   public Flux<CuentaResponse> BuscarPorNombre(@PathVariable String nombre) {

      log.info("REST: Obtener cuentas por nombre del cliente {}", nombre);

      return cuentaUseCase.buscarPorNombreCliente(nombre) // Workaround temporal - idealmente tener getCuenta()
                          .map(mapper::toResponse);
   }

   @PostMapping("/{cuentaId}/perfiles")
   @ResponseStatus(HttpStatus.OK)
   public Mono<CuentaResponse> asociarUsuario(
         @PathVariable Long cuentaId,
         @Valid @RequestBody PerfilRequest request) {

      log.info("REST: Asociar clienteId={} a cuenta={}", request.clienteId(), cuentaId);

      return cuentaUseCase
            .asociarUsuario(cuentaId, request.clienteId(), request.fechaInicio(), request.fechaFin())
            .map(mapper::toResponse)
            .doOnSuccess(res -> log.info("REST: Cliente asignado en cuenta={}", res.id()));
   }

   @PutMapping("/{cuentaId}/perfiles/{perfilId}")
   @ResponseStatus(HttpStatus.OK)
   public Mono<CuentaResponse> editarPerfil(
         @PathVariable Long cuentaId,
         @PathVariable Long perfilId,
         @Valid @RequestBody PerfilRequest request) {

      log.info("REST: Editar perfilId={} en cuentaId={} → clienteId={}",
            perfilId, cuentaId, request.clienteId());

      return cuentaUseCase
            .editarPerfil(cuentaId, perfilId, request.clienteId(), request.fechaInicio(), request.fechaFin())
            .map(mapper::toResponse);
   }

   @DeleteMapping("/{cuentaId}/perfiles/{perfilId}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public Mono<Void> liberarPerfil(@PathVariable Long cuentaId, @PathVariable Long perfilId) {

      log.info("REST: Liberar perfil={} de cuenta={}", perfilId, cuentaId);


      return cuentaUseCase
            .liberarPerfil(cuentaId, perfilId)
            .then()
            .doOnSuccess(v -> log.info("REST: Perfil liberado perfil={} cuenta={}", perfilId, cuentaId));
   }

   @GetMapping("/{id}")
   public Mono<CuentaResponse> obtenerCuenta(@PathVariable Long id) {

      log.info("REST: Obtener cuenta id={}", id);

      return cuentaUseCase.obtenerCuenta(id) // ✅ ideal
                          .map(mapper::toResponse);
   }

   @GetMapping
   @ResponseStatus(HttpStatus.OK)
   public Flux<CuentaResponse> listarCuentas(@RequestParam(required = false) Long servicio) {

      log.info("REST: Listar cuentas servicio={}", servicio);

      return cuentaUseCase.listarCuentas(servicio).map(mapper::toResponse);
   }
}
