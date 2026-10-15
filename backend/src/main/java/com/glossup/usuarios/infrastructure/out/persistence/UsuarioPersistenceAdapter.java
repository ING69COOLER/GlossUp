package com.glossup.usuarios.infrastructure.out.persistence;

import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.out.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Adaptador de salida: implementa {@link UsuarioRepository} con Spring Data JPA. Al guardar,
 * resuelve los roles del dominio contra el catálogo {@code rol} para poblar la tabla de unión.
 */
@Component
public class UsuarioPersistenceAdapter implements UsuarioRepository {

    private final SpringDataUsuarioRepository usuarios;
    private final SpringDataRolRepository roles;

    public UsuarioPersistenceAdapter(SpringDataUsuarioRepository usuarios, SpringDataRolRepository roles) {
        this.usuarios = usuarios;
        this.roles = roles;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity entidad = usuario.getId() == null
                ? new UsuarioJpaEntity()
                : usuarios.findById(usuario.getId()).orElseGet(UsuarioJpaEntity::new);

        UsuarioPersistenceMapper.copiarEscalares(usuario, entidad);
        entidad.setRoles(resolverRoles(usuario.getRoles()));

        return UsuarioPersistenceMapper.aDominio(usuarios.save(entidad));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(Email email) {
        return usuarios.findByEmail(email.valor()).map(UsuarioPersistenceMapper::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarios.findById(id).map(UsuarioPersistenceMapper::aDominio);
    }

    @Override
    public boolean existePorEmail(Email email) {
        return usuarios.existsByEmail(email.valor());
    }

    private Set<RolJpaEntity> resolverRoles(Set<Rol> rolesDominio) {
        Set<RolJpaEntity> resultado = new HashSet<>();
        for (Rol rol : rolesDominio) {
            RolJpaEntity entidad = roles.findByNombre(rol.name())
                    .orElseThrow(() -> new IllegalStateException(
                            "Rol no sembrado en la base de datos: " + rol.name()));
            resultado.add(entidad);
        }
        return resultado;
    }
}
