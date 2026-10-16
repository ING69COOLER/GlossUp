package com.glossup.usuarios.domain.port.in;

public interface AutenticarUsuarioUseCase {

    AutenticacionResultado autenticar(AutenticarUsuarioCommand command);
}
