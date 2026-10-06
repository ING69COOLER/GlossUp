package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/**
 * Objeto de valor: la presencia de un ingrediente en un producto, con su concentración opcional (%).
 * Forma parte de la ficha INCI del producto.
 */
public record ComposicionProducto(Ingrediente ingrediente, Double concentracion) {

    public ComposicionProducto {
        Objects.requireNonNull(ingrediente, "ingrediente requerido");
        if (concentracion != null && (concentracion < 0 || concentracion > 100)) {
            throw new ReglaNegocioException("La concentración debe estar entre 0 y 100 (%)");
        }
    }

    public static ComposicionProducto de(Ingrediente ingrediente) {
        return new ComposicionProducto(ingrediente, null);
    }
}
