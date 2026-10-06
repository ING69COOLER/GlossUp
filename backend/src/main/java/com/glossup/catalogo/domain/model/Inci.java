package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

/**
 * Objeto de valor: nombre de ingrediente según la nomenclatura INCI.
 * Se normaliza (trim + minúsculas) para evitar duplicados por tipeo (ver métrica
 * "Tasa de duplicidad en la ontología").
 */
public record Inci(String nombre) {

    public Inci {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre INCI no puede estar vacío");
        }
        nombre = nombre.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
