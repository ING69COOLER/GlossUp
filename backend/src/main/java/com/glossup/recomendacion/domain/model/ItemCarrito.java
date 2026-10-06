package com.glossup.recomendacion.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/**
 * Línea del carrito: un producto con su cantidad y el precio unitario capturado al agregarlo.
 */
public class ItemCarrito {

    private final Long productoId;
    private final Dinero precioUnitario;
    private int cantidad;

    public ItemCarrito(Long productoId, Dinero precioUnitario, int cantidad) {
        this.productoId = Objects.requireNonNull(productoId, "productoId requerido");
        this.precioUnitario = Objects.requireNonNull(precioUnitario, "precio requerido");
        this.cantidad = validarCantidad(cantidad);
    }

    public void incrementar(int unidades) {
        this.cantidad += validarCantidad(unidades);
    }

    public void cambiarCantidad(int nuevaCantidad) {
        this.cantidad = validarCantidad(nuevaCantidad);
    }

    public Dinero subtotal() {
        return precioUnitario.por(cantidad);
    }

    private static int validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        return cantidad;
    }

    public Long getProductoId() { return productoId; }
    public Dinero getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
}
