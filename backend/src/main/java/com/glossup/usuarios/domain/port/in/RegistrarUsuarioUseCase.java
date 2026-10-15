package com.glossup.usuarios.domain.port.in;

import com.glossup.usuarios.domain.model.Usuario;

/** Puerto de entrada: registro de usuarios. */
public interface RegistrarUsuarioUseCase {

    /**
     * Crea un usuario nuevo con la contraseña cifrada.
     *
     * @throws com.glossup.shared.domain.OperacionNoPermitidaException si el correo ya está registrado
     * @throws com.glossup.shared.domain.DatoInvalidoException si el correo o el nombre no son válidos
     */
    Usuario registrar(RegistrarUsuarioComando comando);
}
