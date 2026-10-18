package com.glossup.usuarios.infrastructure.in.rest.dto;

import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioComando;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para registrar un usuario.
 *
 * <p>{@code rol} es opcional (por defecto CLIENTE). Por auto-registro solo se admite CLIENTE o
 * VENDEDOR; ADMIN se rechaza (422) y lo provisiona otro administrador.</p>
 */
public record RegistrarRequest(

        @Email(message = "el correo no tiene un formato válido")
        @NotBlank(message = "el correo es obligatorio")
        String email,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 150, message = "el nombre admite máximo 150 caracteres")
        String nombre,

        @NotBlank(message = "la contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "la contraseña debe tener entre 8 y 72 caracteres")
        String password,

        Rol rol) {

    public RegistrarUsuarioComando aComando() {
        return new RegistrarUsuarioComando(email, nombre, password, rol);
    }
}
