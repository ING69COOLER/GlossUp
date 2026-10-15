package com.glossup.usuarios.domain.port.out;

import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Usuario;

import java.util.Optional;

/**
 * Puerto de salida del módulo de usuarios: contrato de persistencia de la identidad, sin conocer
 * la tecnología concreta (JPA/PostgreSQL es un detalle del adaptador).
 */
public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(Email email);

    Optional<Usuario> buscarPorId(Long id);

    boolean existePorEmail(Email email);
}
