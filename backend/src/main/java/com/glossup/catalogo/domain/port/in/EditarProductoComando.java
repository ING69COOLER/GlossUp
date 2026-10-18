package com.glossup.catalogo.domain.port.in;

/**
 * Comando para editar los datos comerciales de un producto (HU4). El {@code vendedorId} lo fija el
 * adaptador desde el usuario autenticado: un vendedor solo puede editar sus propios productos.
 */
public record EditarProductoComando(
        Long productoId,
        Long vendedorId,
        String nombre,
        String marca,
        String categoria,
        double precio,
        Double ph,
        String registroInvima,
        String imagenUrl) {
}
