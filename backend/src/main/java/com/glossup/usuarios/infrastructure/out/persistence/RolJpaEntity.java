package com.glossup.usuarios.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA de la tabla de catálogo {@code rol} (CLIENTE, VENDEDOR, ADMIN) sembrada por Flyway.
 * Es de solo lectura desde la aplicación: los roles no se crean en tiempo de ejecución.
 */
@Entity
@Table(name = "rol")
public class RolJpaEntity {

    @Id
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    protected RolJpaEntity() {
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
