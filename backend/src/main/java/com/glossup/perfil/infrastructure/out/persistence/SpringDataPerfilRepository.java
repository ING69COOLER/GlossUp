package com.glossup.perfil.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA sobre {@link PerfilDermatologicoJpaEntity}. Spring genera la
 * implementación en tiempo de ejecución; es un detalle interno del adaptador de persistencia.
 */
public interface SpringDataPerfilRepository extends JpaRepository<PerfilDermatologicoJpaEntity, Long> {

    Optional<PerfilDermatologicoJpaEntity> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
