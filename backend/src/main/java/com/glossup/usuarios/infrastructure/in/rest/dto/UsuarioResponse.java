package com.glossup.usuarios.infrastructure.in.rest.dto;

import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;

import java.util.List;

/**
 * Representación pública de un usuario (nunca expone el hash de la contraseña).
 *
 * @param panel panel al que debe entrar el usuario según su rol principal (ADMIN &gt; VENDEDOR &gt; CLIENTE)
 */
public record UsuarioResponse(
        Long usuarioId,
        String nombre,
        String email,
        List<String> roles,
        String panel) {

    public static UsuarioResponse desde(Usuario u) {
        List<String> roles = u.getRoles().stream().map(Rol::name).sorted().toList();
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail().valor(), roles,
                PanelPrincipal.de(u.getRoles()));
    }
}
