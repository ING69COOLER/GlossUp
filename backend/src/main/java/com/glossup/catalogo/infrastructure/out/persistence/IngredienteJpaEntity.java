package com.glossup.catalogo.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad JPA de la tabla {@code ingrediente} (ontología INCI). */
@Entity
@Table(name = "ingrediente")
public class IngredienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_inci", nullable = false, unique = true)
    private String nombreInci;

    @Column(name = "descripcion")
    private String descripcion;

    protected IngredienteJpaEntity() {
    }

    public IngredienteJpaEntity(String nombreInci, String descripcion) {
        this.nombreInci = nombreInci;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public String getNombreInci() { return nombreInci; }
    public String getDescripcion() { return descripcion; }
}
