package com.glossup.usuarios.infrastructure.out.persistence.adapter;

import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.out.UsuarioRepositoryPort;
import com.glossup.usuarios.infrastructure.out.persistence.entity.UsuarioEntity;
import com.glossup.usuarios.infrastructure.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository repository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::aDominio);
    }

    private Usuario aDominio(UsuarioEntity entity) {
        EnumSet<Rol> roles = EnumSet.noneOf(Rol.class);
        entity.getRoles().forEach(rol -> roles.add(Rol.valueOf(rol.getNombre())));
        return new Usuario(entity.getId(), entity.getEmail(), entity.getNombre(),
                entity.getPasswordHash(), entity.isActivo(), roles, entity.getCreadoEn());
    }
}
