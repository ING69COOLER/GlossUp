package com.glossup.perfil.infrastructure.out.persistence;

import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.port.out.PerfilDermatologicoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto {@link PerfilDermatologicoRepository} del dominio
 * apoyándose en Spring Data JPA. Es el único punto donde el perfil toca la tecnología de
 * persistencia; convierte agregado ⇄ entidad con {@link PerfilPersistenceMapper}.
 */
@Component
public class PerfilPersistenceAdapter implements PerfilDermatologicoRepository {

    private final SpringDataPerfilRepository jpa;

    public PerfilPersistenceAdapter(SpringDataPerfilRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PerfilDermatologico guardar(PerfilDermatologico perfil) {
        PerfilDermatologicoJpaEntity guardada = jpa.save(PerfilPersistenceMapper.aEntidad(perfil));
        return PerfilPersistenceMapper.aDominio(guardada);
    }

    @Override
    public Optional<PerfilDermatologico> buscarPorUsuarioId(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).map(PerfilPersistenceMapper::aDominio);
    }

    @Override
    public boolean existePorUsuarioId(Long usuarioId) {
        return jpa.existsByUsuarioId(usuarioId);
    }
}
