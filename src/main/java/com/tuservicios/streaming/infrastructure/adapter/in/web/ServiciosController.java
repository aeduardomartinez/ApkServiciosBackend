package com.tuservicios.streaming.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tuservicios.streaming.application.port.in.ServiciosUseCase;
import com.tuservicios.streaming.domain.model.Servicio;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.ServicioRequest;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.CuentaResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ServicioListItemResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.dto.response.ServicioResponse;
import com.tuservicios.streaming.infrastructure.adapter.in.web.mapper.ServicioWebMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/servicios")
public class ServiciosController {

   private final ServiciosUseCase serviciosUseCase;

   private final ServicioWebMapper webMapper;

   public ServiciosController(ServiciosUseCase serviciosUseCase, ServicioWebMapper webMapper) {
      this.serviciosUseCase = serviciosUseCase;
      this.webMapper = webMapper;
   }

   @PostMapping
   public Mono<ResponseEntity<ServicioResponse>> crearServicio(@RequestBody ServicioRequest r) {

      Servicio servicio = Servicio.crearNuevo(r.nombreServicio(), r.maxPerfilesBase(), r.maxPerfilesExtra(), r.valorTotalCuenta(), r.valorPerfil());

      return serviciosUseCase.crearServicio(servicio).map(s -> {
         ServicioResponse response = new ServicioResponse(s.getId(),
               s.getNombreServicio(), "El Servicio "+ s.getNombreServicio()+ "fue creado" );
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
      });
   }

   @GetMapping
   public Flux<ServicioListItemResponse> listarServicios() {
      return serviciosUseCase.listarServicios().map(webMapper::toListItem);
   }

   // opcional: obtener uno
   @GetMapping("/{id}")
   public Mono<ServicioListItemResponse> obtenerServicio(@PathVariable Long id) {
      return serviciosUseCase.obtenerServicio(id).map(webMapper::toListItem);
   }
}