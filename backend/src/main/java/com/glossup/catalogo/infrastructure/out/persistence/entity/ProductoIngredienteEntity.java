package com.glossup.catalogo.infrastructure.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_ingrediente")
public class ProductoIngredienteEntity {

    @EmbeddedId
    private ProductoIngredienteId id = new ProductoIngredienteId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredienteId")
    @JoinColumn(name = "ingrediente_id")
    private IngredienteEntity ingrediente;

    @Column(name = "concentracion")
    private Double concentracion;

    public ProductoIngredienteEntity() {}

    public ProductoIngredienteEntity(ProductoEntity producto, IngredienteEntity ingrediente, Double concentracion) {
        this.producto = producto;
        this.ingrediente = ingrediente;
        this.concentracion = concentracion;
        if (producto != null && producto.getId() != null && ingrediente != null && ingrediente.getId() != null) {
            this.id = new ProductoIngredienteId(producto.getId(), ingrediente.getId());
        }
    }

    public ProductoIngredienteId getId() { return id; }
    public void setId(ProductoIngredienteId id) { this.id = id; }

    public ProductoEntity getProducto() { return producto; }
    public void setProducto(ProductoEntity producto) { this.producto = producto; }

    public IngredienteEntity getIngrediente() { return ingrediente; }
    public void setIngrediente(IngredienteEntity ingrediente) { this.ingrediente = ingrediente; }

    public Double getConcentracion() { return concentracion; }
    public void setConcentracion(Double concentracion) { this.concentracion = concentracion; }
}

