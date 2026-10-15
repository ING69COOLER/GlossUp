package com.glossup.usuarios.domain.port.in;

import com.glossup.usuarios.domain.model.Rol;

/**
 * Comando para registrar un usuario nuevo.
 *
 * @param email        correo (será validado y normalizado por el VO Email)
 * @param nombre       nombre visible del usuario
 * @param password     contraseña en claro (se cifra en la capa de aplicación; nunca se persiste)
 * @param rolInicial   rol con el que nace la cuenta; si es {@code null} se asume {@link Rol#CLIENTE}
 */
public record RegistrarUsuarioComando(
        String email,
        String nombre,
        String password,
        Rol rolInicial) {
}
