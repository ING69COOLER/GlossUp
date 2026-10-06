package com.glossup.recomendacion.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/** Línea inmutable de un pedido confirmado (foto del producto al momento de la compra). */
public record PedidoItem(Long productoId, int cantidad, Dinero precioUnitario) {

    public PedidoItem {
        Objects.requireNonNull(productoId, "productoId requerido");
        Objects.requireNonNull(precioUnitario, "precio requerido");
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
    }

    public Dinero subtotal() {
        return precioUnitario.por(cantidad);
    }
}
