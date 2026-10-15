package com.glossup.catalogo.infrastructure.in.rest.dto;

import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Cuerpo para editar un producto (HU4). */
public record EditarProductoRequest(

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 200) String nombre,

        @Size(max = 150) String marca,
        @Size(max = 120) String categoria,

        @NotNull(message = "el precio es obligatorio")
        @PositiveOrZero(message = "el precio no puede ser negativo")
        Double precio,

        @DecimalMin(value = "0.0", message = "el pH mínimo es 0")
        @DecimalMax(value = "14.0", message = "el pH máximo es 14")
        Double ph,

        @Size(max = 100) String registroInvima) {

    public EditarProductoComando aComando(Long productoId, Long vendedorId) {
        return new EditarProductoComando(productoId, vendedorId, nombre, marca, categoria, precio, ph, registroInvima);
    }
}
