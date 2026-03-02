package com.tuservicios.streaming.infrastructure.adapter.in.web;

import com.tuservicios.streaming.application.port.in.ClienteRegistradoUseCase;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.ClienteRegistradoRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ClienteRegistradoResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.mapper.ClienteRegistradoWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

   private final ClienteRegistradoUseCase useCase;
   private final ClienteRegistradoWebMapper mapper;

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Mono<ClienteRegistradoResponse> crear(@Valid @RequestBody ClienteRegistradoRequest req) {
      log.info("REST: Crear cliente {} {}", req.nombre(), req.apellido());
      return useCase.crearCliente(req.nombre(), req.apellido(), req.telefono())
                    .map(mapper::toResponse);
   }

   @GetMapping
   public Flux<ClienteRegistradoResponse> listar() {
      return useCase.listarClientes().map(mapper::toResponse);
   }

   @GetMapping("/{id}")
   public Mono<ClienteRegistradoResponse> obtener(@PathVariable Long id) {
      return useCase.obtenerCliente(id).map(mapper::toResponse);
   }

   @PutMapping("/{id}")
   public Mono<ClienteRegistradoResponse> actualizar(
         @PathVariable Long id,
         @Valid @RequestBody ClienteRegistradoRequest req) {
      return useCase.actualizarCliente(id, req.nombre(), req.apellido(), req.telefono())
                    .map(mapper::toResponse);
   }

   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public Mono<Void> eliminar(@PathVariable Long id) {
      return useCase.eliminarCliente(id);
   }
}