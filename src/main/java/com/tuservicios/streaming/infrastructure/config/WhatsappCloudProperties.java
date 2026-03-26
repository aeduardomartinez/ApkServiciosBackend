package com.tuservicios.streaming.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whatsapp")
public record WhatsappCloudProperties(
      String baseUrl,
      String apiVersion,
      String phoneNumberId,
      String accessToken,
      String templateName,
      String languageCode
) {}