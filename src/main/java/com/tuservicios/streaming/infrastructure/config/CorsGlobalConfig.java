package com.tuservicios.streaming.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
public class CorsGlobalConfig {

   @Bean
   public CorsWebFilter corsWebFilter() {
      CorsConfiguration config = new CorsConfiguration();

      // Permitir cualquier origen
      config.setAllowedOriginPatterns(List.of("*"));

      config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
      config.setAllowedHeaders(List.of("*"));

      // Desactivamos credenciales para permitir el uso de "*" en origenes, 
      // lo cual es necesario para que Android no bloquee la peticion.
      config.setAllowCredentials(false);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);

      return new CorsWebFilter(source);
   }
}