package com.glossup.catalogo.domain.port.in;

/**
 * Comando para registrar un producto nuevo (HU3). El {@code vendedorId} lo fija el adaptador desde
 * el usuario autenticado. El producto nace inactivo (borrador) hasta tener ficha INCI y publicarse.
 */
public record CrearProductoComando(
        Long vendedorId,
        String nombre,
        String marca,
        String categoria,
        double precio,
        int stock,
        Double ph,
        String registroInvima,
        String imagenUrl) {
}
