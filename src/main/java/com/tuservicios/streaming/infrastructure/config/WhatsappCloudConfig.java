package com.tuservicios.streaming.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(WhatsappCloudProperties.class)
public class WhatsappCloudConfig {

   @Bean
   public WebClient whatsappWebClient(WhatsappCloudProperties props) {
      return WebClient.builder()
                      .baseUrl(props.baseUrl())
                      .build();
   }
}