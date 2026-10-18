package com.glossup.catalogo.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Cuerpo para fijar la disponibilidad (stock absoluto) de un producto. */
public record ActualizarStockRequest(

        @NotNull(message = "el stock es obligatorio")
        @PositiveOrZero(message = "el stock no puede ser negativo")
        Integer stock) {
}
