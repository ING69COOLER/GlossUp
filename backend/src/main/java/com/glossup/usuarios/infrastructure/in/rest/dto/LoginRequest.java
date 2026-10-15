package com.glossup.usuarios.infrastructure.in.rest.dto;

import com.glossup.usuarios.domain.port.in.CredencialesComando;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo para iniciar sesión. */
public record LoginRequest(

        @NotBlank(message = "el correo es obligatorio")
        String email,

        @NotBlank(message = "la contraseña es obligatoria")
        String password) {

    public CredencialesComando aComando() {
        return new CredencialesComando(email, password);
    }
}
