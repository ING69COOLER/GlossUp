package com.glossup.compatibilidad.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

/**
 * Alerta emitida por el motor de compatibilidad.
 *
 * <p>RNF-05: toda alerta debe explicar su causa (no basta con "incompatible").</p>
 *
 * @param severidad gravedad de la alerta
 * @param mensaje   texto comprensible para el usuario no técnico (RNF-07)
 * @param causa     explicación del porqué (ingredientes/condición implicados)
 */
public record Alerta(Severidad severidad, String mensaje, String causa) {

    public Alerta {
        if (severidad == null) throw new ReglaNegocioException("La alerta requiere severidad");
        if (mensaje == null || mensaje.isBlank()) throw new ReglaNegocioException("La alerta requiere un mensaje");
        if (causa == null || causa.isBlank()) throw new ReglaNegocioException("La alerta debe explicar su causa (RNF-05)");
    }

    public boolean esCritica() {
        return severidad == Severidad.ALTA;
    }
}
