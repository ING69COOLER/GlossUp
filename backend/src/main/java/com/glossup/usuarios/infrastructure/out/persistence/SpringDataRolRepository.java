package com.glossup.usuarios.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Acceso de solo lectura al catálogo de roles sembrado por Flyway. */
public interface SpringDataRolRepository extends JpaRepository<RolJpaEntity, Long> {
    Optional<RolJpaEntity> findByNombre(String nombre);
}
