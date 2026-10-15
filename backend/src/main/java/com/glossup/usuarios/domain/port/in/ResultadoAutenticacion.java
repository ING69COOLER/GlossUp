package com.glossup.usuarios.domain.port.in;

import com.glossup.usuarios.domain.model.Usuario;

/**
 * Resultado de una autenticación exitosa: el token de acceso emitido y el usuario autenticado
 * (para que el adaptador web construya la respuesta sin volver a consultar).
 */
public record ResultadoAutenticacion(String token, Usuario usuario) {
}
