package com.glossup.usuarios.domain.port.in;

/** Comando con las credenciales que presenta un usuario para autenticarse. */
public record CredencialesComando(String email, String password) {
}
