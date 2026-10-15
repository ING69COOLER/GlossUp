package com.glossup.catalogo.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data JPA sobre {@link ProductoJpaEntity}. */
public interface SpringDataProductoRepository extends JpaRepository<ProductoJpaEntity, Long> {
}
