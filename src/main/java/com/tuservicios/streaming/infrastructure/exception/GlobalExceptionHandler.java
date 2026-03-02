package com.tuservicios.streaming.infrastructure.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tuservicios.streaming.domain.exception.ServicioAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

   private Map<String, Object> buildResponse(HttpStatus status, String message) {
      Map<String, Object> response = new HashMap<>();
      response.put("timestamp", java.time.LocalDateTime.now());
      response.put("mensaje", message);
      response.put("status", status.value());
      return response;
   }

   @ExceptionHandler(ServicioAlreadyExistsException.class)
   public ResponseEntity<Map<String, Object>> handleServicioExists(ServicioAlreadyExistsException ex) {
      return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(buildResponse(HttpStatus.CONFLICT, ex.getMessage()));
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Map<String, Object>> handleOtherErrors(Exception ex) {
      return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
   }
}
