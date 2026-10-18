package com.glossup.catalogo.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repositorio Spring Data JPA sobre {@link ProductoJpaEntity}. */
public interface SpringDataProductoRepository extends JpaRepository<ProductoJpaEntity, Long> {

    List<ProductoJpaEntity> findByVendedorIdOrderByCreadoEnDesc(Long vendedorId);
}
