package com.glossup.catalogo.infrastructure.in.rest.dto;

import com.glossup.catalogo.domain.model.Producto;

/** Representación de un producto devuelta por la API. */
public record ProductoResponse(
        Long id,
        Long vendedorId,
        String nombre,
        String marca,
        String categoria,
        double precio,
        int stock,
        Double ph,
        String registroInvima,
        boolean activo) {

    public static ProductoResponse desde(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getVendedorId(), p.getNombre(), p.getMarca(), p.getCategoria(),
                p.getPrecio().monto().doubleValue(), p.getStock(), p.getPh(), p.getRegistroInvima(), p.isActivo());
    }
}
