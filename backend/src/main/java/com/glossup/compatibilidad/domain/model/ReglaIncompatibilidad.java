package com.glossup.compatibilidad.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/**
 * Regla de incompatibilidad producto–producto: dos ingredientes (por nombre INCI) que no deben
 * combinarse. Se compara de forma no ordenada (A–B equivale a B–A).
 *
 * <p>Regla: los dos ingredientes deben ser distintos.</p>
 */
public class ReglaIncompatibilidad {

    private final Long id;
    private final String ingredienteA;
    private final String ingredienteB;
    private final Severidad severidad;
    private final String motivo;

    public ReglaIncompatibilidad(Long id, String ingredienteA, String ingredienteB,
                                 Severidad severidad, String motivo) {
        this.ingredienteA = normalizar(ingredienteA, "ingredienteA");
        this.ingredienteB = normalizar(ingredienteB, "ingredienteB");
        if (this.ingredienteA.equals(this.ingredienteB)) {
            throw new ReglaNegocioException("Una incompatibilidad requiere dos ingredientes distintos");
        }
        this.id = id;
        this.severidad = Objects.requireNonNull(severidad, "severidad requerida");
        this.motivo = (motivo == null || motivo.isBlank()) ? "Incompatibilidad conocida entre ingredientes" : motivo.trim();
    }

    /** ¿Esta regla aplica al par de ingredientes dado (en cualquier orden)? */
    public boolean aplicaA(String inci1, String inci2) {
        String a = normalizar(inci1, "inci1");
        String b = normalizar(inci2, "inci2");
        return (ingredienteA.equals(a) && ingredienteB.equals(b))
                || (ingredienteA.equals(b) && ingredienteB.equals(a));
    }

    private static String normalizar(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return s.trim().toLowerCase();
    }

    public Long getId() { return id; }
    public String getIngredienteA() { return ingredienteA; }
    public String getIngredienteB() { return ingredienteB; }
    public Severidad getSeveridad() { return severidad; }
    public String getMotivo() { return motivo; }
}
