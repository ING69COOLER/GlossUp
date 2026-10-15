package com.glossup.usuarios.infrastructure.in.rest.dto;

import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.port.in.ResultadoAutenticacion;

import java.util.List;

/**
 * Respuesta del login: el token de acceso y los datos del usuario, incluido el {@code panel} al que
 * debe redirigir el frontend según su rol.
 */
public record AuthResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nombre,
        String email,
        List<String> roles,
        String panel) {

    public static AuthResponse desde(ResultadoAutenticacion resultado) {
        var u = resultado.usuario();
        List<String> roles = u.getRoles().stream().map(Rol::name).sorted().toList();
        return new AuthResponse(
                resultado.token(),
                "Bearer",
                u.getId(),
                u.getNombre(),
                u.getEmail().valor(),
                roles,
                PanelPrincipal.de(u.getRoles()));
    }
}
