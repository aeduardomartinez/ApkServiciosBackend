package com.tuservicios.streaming.infrastructure.adapter.in.web.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

public record ServicioRequest (

      @NotBlank(message = "El nombre del servicio es obligatorio")
      String nombreServicio,

      @NotNull(message = "El número de perfiles base es obligatorio")
      @Min(value = 1, message = "Debe tener al menos 1 perfil base")
      Integer maxPerfilesBase,

      @NotNull(message = "El número de perfiles extra es obligatorio")
      @Min(value = 0, message = "No puede ser negativo")
      Integer maxPerfilesExtra,

      // 👇 AQUÍ ESTABA EL ERROR
      @NotNull(message = "El valor es obligatorio")
      @DecimalMin(value = "0.0", inclusive = false)
      double valorTotalCuenta,

      @NotNull(message = "El valor es obligatorio")
      @DecimalMin(value = "0.0", inclusive = false)
      double valorPerfil
)

{

}
