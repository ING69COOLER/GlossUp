package com.glossup.usuarios.domain.port.in;

import com.glossup.usuarios.domain.model.Usuario;

/** Puerto de entrada: autenticación (login) y consulta del usuario actual. */
public interface AutenticarUsuarioUseCase {

    /**
     * Verifica las credenciales y emite un token de acceso.
     *
     * @throws com.glossup.shared.exception.NoAutorizadoException si las credenciales son inválidas
     *         o la cuenta está inactiva
     */
    ResultadoAutenticacion autenticar(CredencialesComando comando);

    /**
     * Devuelve el usuario a partir de su id (el del token).
     *
     * @throws com.glossup.shared.exception.NotFoundException si el usuario no existe
     */
    Usuario consultarPorId(Long usuarioId);
}
