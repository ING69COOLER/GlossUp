package com.glossup.catalogo.infrastructure.in.rest.dto;

import com.glossup.catalogo.domain.model.ComposicionProducto;

public record IngredienteResponse(
        Long id,
        String inci,
        String descripcion,
        Double concentracion
) {
    public static IngredienteResponse fromDomain(ComposicionProducto cp) {
        return new IngredienteResponse(
                cp.ingrediente().getId(),
                cp.ingrediente().getInci().nombre(),
                cp.ingrediente().getDescripcion(),
                cp.concentracion()
        );
    }
}
