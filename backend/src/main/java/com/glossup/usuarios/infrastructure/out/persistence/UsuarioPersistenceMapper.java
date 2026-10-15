package com.glossup.usuarios.infrastructure.out.persistence;

import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/** Traduce entre el agregado de dominio {@link Usuario} y {@link UsuarioJpaEntity}. */
final class UsuarioPersistenceMapper {

    private UsuarioPersistenceMapper() {
    }

    static Usuario aDominio(UsuarioJpaEntity e) {
        Set<Rol> roles = e.getRoles().stream()
                .map(r -> Rol.valueOf(r.getNombre()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Rol.class)));
        return new Usuario(
                e.getId(),
                new Email(e.getEmail()),
                e.getNombre(),
                e.getPasswordHash(),
                e.isActivo(),
                roles,
                e.getCreadoEn());
    }

    /** Copia los campos escalares del dominio a la entidad (los roles los resuelve el adaptador). */
    static void copiarEscalares(Usuario usuario, UsuarioJpaEntity entidad) {
        entidad.setId(usuario.getId());
        entidad.setEmail(usuario.getEmail().valor());
        entidad.setNombre(usuario.getNombre());
        entidad.setPasswordHash(usuario.getPasswordHash());
        entidad.setActivo(usuario.isActivo());
        entidad.setCreadoEn(usuario.getCreadoEn());
    }
}
