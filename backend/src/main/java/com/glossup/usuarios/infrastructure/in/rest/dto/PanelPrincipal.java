package com.glossup.usuarios.infrastructure.in.rest.dto;

import com.glossup.usuarios.domain.model.Rol;

import java.util.Set;

/**
 * Determina el panel al que entra un usuario según sus roles. Si acumula varios, manda el de mayor
 * privilegio: ADMIN &gt; VENDEDOR &gt; CLIENTE. Es una regla de presentación (enrutado), por eso vive
 * en la capa web y no en el dominio.
 */
final class PanelPrincipal {

    private PanelPrincipal() {
    }

    static String de(Set<Rol> roles) {
        if (roles.contains(Rol.ADMIN)) return Rol.ADMIN.name();
        if (roles.contains(Rol.VENDEDOR)) return Rol.VENDEDOR.name();
        return Rol.CLIENTE.name();
    }
}
