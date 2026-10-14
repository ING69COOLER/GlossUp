package com.glossup.perfil.domain.port.in;

import com.glossup.perfil.domain.model.TipoPiel;

import java.util.Set;

/**
 * Comando de entrada para el caso de uso "crear perfil dermatológico" (HU1).
 *
 * <p>Es un DTO de aplicación libre de frameworks: traslada la intención del usuario hacia el
 * dominio sin arrastrar anotaciones web ni de persistencia. El {@code usuarioId} lo fija el
 * adaptador de entrada a partir del usuario autenticado (nunca lo elige el cliente).</p>
 *
 * @param usuarioId   identificador del usuario dueño del perfil
 * @param ph          pH de la piel (0–14); opcional
 * @param tipoPiel    tipo de piel; opcional
 * @param tipoCabello tipo de cabello; opcional
 * @param afecciones  afecciones/condiciones dérmicas iniciales; puede ir vacío
 * @param alergias    alergias/sensibilidades iniciales (nombres INCI); puede ir vacío
 */
public record CrearPerfilComando(
        Long usuarioId,
        Double ph,
        TipoPiel tipoPiel,
        String tipoCabello,
        Set<String> afecciones,
        Set<String> alergias) {
}
