package com.glossup.recomendacion.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/**
 * Recomendación de un producto generada por el componente de IA, con una razón explicativa y un
 * puntaje de afinidad (0.0–1.0) respecto al perfil/necesidades del usuario.
 */
public record Recomendacion(Long productoId, String razon, double score) {

    public Recomendacion {
        Objects.requireNonNull(productoId, "productoId requerido");
        if (razon == null || razon.isBlank()) {
            throw new ReglaNegocioException("La recomendación debe incluir una razón");
        }
        if (score < 0.0 || score > 1.0) {
            throw new ReglaNegocioException("El score debe estar entre 0.0 y 1.0");
        }
    }
}
