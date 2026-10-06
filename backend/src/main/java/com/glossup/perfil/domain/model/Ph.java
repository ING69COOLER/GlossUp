package com.glossup.perfil.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

/**
 * Objeto de valor para el pH de la piel.
 *
 * <p>Regla de negocio: el pH está en la escala 0–14. La piel normal ronda 4.7–5.75 (glosario);
 * fuera de 0–14 el valor es inválido.</p>
 */
public record Ph(double valor) {

    public Ph {
        if (valor < 0 || valor > 14) {
            throw new ReglaNegocioException("El pH debe estar entre 0 y 14 (recibido: " + valor + ")");
        }
    }

    /** Indica si el pH está dentro del rango típico de piel sana (4.7–5.75). */
    public boolean esFisiologico() {
        return valor >= 4.7 && valor <= 5.75;
    }
}
