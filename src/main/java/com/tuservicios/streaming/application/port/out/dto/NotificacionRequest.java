package com.tuservicios.streaming.application.port.out.dto;

import java.util.List;

public record NotificacionRequest(
      String telefonoE164,
      Canal canal,
      Plantilla plantilla,
      List<String> parametros
) {

}