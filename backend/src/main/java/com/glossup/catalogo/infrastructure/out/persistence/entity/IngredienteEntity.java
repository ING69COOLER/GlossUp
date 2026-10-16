package com.glossup.catalogo.infrastructure.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ingrediente")
public class IngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_inci", nullable = false, unique = true, length = 200)
    private String nombreInci;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    public IngredienteEntity() {}

    public IngredienteEntity(Long id, String nombreInci, String descripcion) {
        this.id = id;
        this.nombreInci = nombreInci;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreInci() { return nombreInci; }
    public void setNombreInci(String nombreInci) { this.nombreInci = nombreInci; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

