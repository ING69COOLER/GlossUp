package com.glossup.perfil.domain.port.in;

import com.glossup.perfil.domain.model.TipoPiel;

/**
 * Comando para actualizar el biotipo del perfil (parte de la HU2: mantener el perfil al día).
 * Todos los campos son opcionales; {@code null} significa "dejar sin especificar".
 *
 * @param usuarioId   usuario dueño del perfil (lo fija el adaptador desde el usuario autenticado)
 * @param ph          pH de la piel (0–14)
 * @param tipoPiel    tipo de piel
 * @param tipoCabello tipo de cabello
 */
public record ActualizarBiotipoComando(
        Long usuarioId,
        Double ph,
        TipoPiel tipoPiel,
        String tipoCabello) {
}
