package com.glossup.catalogo.infrastructure.in.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo para añadir un ingrediente (INCI) a la ficha de un producto. */
public record AgregarIngredienteRequest(

        @NotBlank(message = "el nombre INCI es obligatorio")
        @Size(max = 200) String nombreInci,

        @DecimalMin(value = "0.0", message = "la concentración mínima es 0")
        @DecimalMax(value = "100.0", message = "la concentración máxima es 100")
        Double concentracion,

        @Size(max = 120) String funcion) {
}
