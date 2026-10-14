package com.glossup.perfil.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para agregar o quitar un elemento del perfil (una alergia o una afección) en la HU2.
 *
 * <p>El valor viaja en el cuerpo, nunca en la URL: alergias y afecciones son datos sensibles de
 * salud (Ley 1581/2012) y no deben quedar en rutas, query strings ni logs de acceso. Por eso
 * incluso el borrado se hace con cuerpo en lugar de un path variable.</p>
 */
public record ItemPerfilRequest(
        @NotBlank(message = "el valor es obligatorio")
        @Size(max = 120, message = "máximo 120 caracteres")
        String valor) {
}
