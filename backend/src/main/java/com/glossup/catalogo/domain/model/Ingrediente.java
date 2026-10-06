package com.glossup.catalogo.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio: ingrediente de la ontología (identificado por su nombre INCI).
 * Es la unidad que el motor de compatibilidad cruza entre productos y contra el perfil.
 */
public class Ingrediente {

    private final Long id;
    private final Inci inci;
    private String descripcion;

    public Ingrediente(Long id, Inci inci, String descripcion) {
        this.id = id;
        this.inci = Objects.requireNonNull(inci, "INCI requerido");
        this.descripcion = (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

    public static Ingrediente nuevo(Inci inci, String descripcion) {
        return new Ingrediente(null, inci, descripcion);
    }

    public Long getId() { return id; }
    public Inci getInci() { return inci; }
    public String getDescripcion() { return descripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingrediente otro)) return false;
        return inci.equals(otro.inci);
    }

    @Override
    public int hashCode() {
        return inci.hashCode();
    }
}
