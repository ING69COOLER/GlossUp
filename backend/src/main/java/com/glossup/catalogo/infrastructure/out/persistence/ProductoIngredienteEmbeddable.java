package com.glossup.catalogo.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

/** Fila de {@code producto_ingrediente} vista como elemento de la colección del producto. */
@Embeddable
public class ProductoIngredienteEmbeddable {

    @Column(name = "ingrediente_id", nullable = false)
    private Long ingredienteId;

    @Column(name = "concentracion", precision = 5, scale = 2)
    private BigDecimal concentracion;

    protected ProductoIngredienteEmbeddable() {
    }

    public ProductoIngredienteEmbeddable(Long ingredienteId, BigDecimal concentracion) {
        this.ingredienteId = ingredienteId;
        this.concentracion = concentracion;
    }

    public Long getIngredienteId() { return ingredienteId; }
    public BigDecimal getConcentracion() { return concentracion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoIngredienteEmbeddable otro)) return false;
        return Objects.equals(ingredienteId, otro.ingredienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ingredienteId);
    }
}
