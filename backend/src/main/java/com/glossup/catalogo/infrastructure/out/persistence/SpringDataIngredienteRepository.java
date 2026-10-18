package com.glossup.catalogo.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repositorio Spring Data JPA sobre {@link IngredienteJpaEntity}. */
public interface SpringDataIngredienteRepository extends JpaRepository<IngredienteJpaEntity, Long> {

    Optional<IngredienteJpaEntity> findByNombreInci(String nombreInci);
}
