package com.glossup.catalogo.infrastructure.in.rest.dto;

import com.glossup.catalogo.domain.model.Producto;

import java.util.List;

/** Representación de un producto devuelta por la API, incluida su ficha INCI. */
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
        String imagenUrl,
        boolean activo,
        List<IngredienteResponse> ingredientes) {

    public record IngredienteResponse(String nombreInci, String funcion, Double concentracion) {
    }

    public static ProductoResponse desde(Producto p) {
        List<IngredienteResponse> ficha = p.getComposicion().stream()
                .map(c -> new IngredienteResponse(
                        c.ingrediente().getInci().nombre(), c.ingrediente().getDescripcion(), c.concentracion()))
                .toList();
        return new ProductoResponse(
                p.getId(), p.getVendedorId(), p.getNombre(), p.getMarca(), p.getCategoria(),
                p.getPrecio().monto().doubleValue(), p.getStock(), p.getPh(), p.getRegistroInvima(),
                p.getImagenUrl(), p.isActivo(), ficha);
    }
}
