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

      config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      config.setAllowedHeaders(List.of("*"));

      // Si usas cookies o Authorization header JWT:
      config.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);

      return new CorsWebFilter(source);
   }
}