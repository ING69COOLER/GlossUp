package com.glossup.recomendacion.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Raíz de agregado: carrito de compras de un usuario (HU9).
 *
 * <p>Reglas: si se agrega un producto que ya está, se acumula la cantidad; el total es la suma de
 * los subtotales; no se puede finalizar (checkout) un carrito vacío.</p>
 */
public class Carrito {

    private final Long usuarioId;
    private final List<ItemCarrito> items;

    public Carrito(Long usuarioId, List<ItemCarrito> items) {
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId requerido");
        this.items = new ArrayList<>(items == null ? List.of() : items);
    }

    public static Carrito vacio(Long usuarioId) {
        return new Carrito(usuarioId, new ArrayList<>());
    }

    public void agregar(Long productoId, Dinero precioUnitario, int cantidad) {
        Optional<ItemCarrito> existente = buscar(productoId);
        if (existente.isPresent()) {
            existente.get().incrementar(cantidad);
        } else {
            items.add(new ItemCarrito(productoId, precioUnitario, cantidad));
        }
    }

    public void quitar(Long productoId) {
        items.removeIf(i -> i.getProductoId().equals(productoId));
    }

    public void cambiarCantidad(Long productoId, int cantidad) {
        ItemCarrito item = buscar(productoId)
                .orElseThrow(() -> new ReglaNegocioException("El producto no está en el carrito"));
        item.cambiarCantidad(cantidad);
    }

    public void vaciar() {
        items.clear();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public Dinero total() {
        return items.stream()
                .map(ItemCarrito::subtotal)
                .reduce(Dinero.cero(), Dinero::mas);
    }

    private Optional<ItemCarrito> buscar(Long productoId) {
        return items.stream().filter(i -> i.getProductoId().equals(productoId)).findFirst();
    }

    public Long getUsuarioId() { return usuarioId; }
    public List<ItemCarrito> getItems() { return List.copyOf(items); }
}
