package com.glossup.compatibilidad.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.util.Objects;

/**
 * Regla producto–perfil: un ingrediente (INCI) desaconsejado para una condición del usuario
 * (un tipo de piel o una afección). La "condicion" se compara contra el perfil dermatológico.
 */
public class ReglaPerfil {

    private final Long id;
    private final String ingrediente;
    private final String condicion;
    private final Severidad severidad;
    private final String motivo;

    public ReglaPerfil(Long id, String ingrediente, String condicion, Severidad severidad, String motivo) {
        this.id = id;
        this.ingrediente = normalizar(ingrediente, "ingrediente");
        this.condicion = normalizar(condicion, "condicion");
        this.severidad = Objects.requireNonNull(severidad, "severidad requerida");
        this.motivo = (motivo == null || motivo.isBlank()) ? "Ingrediente no recomendado para la condición" : motivo.trim();
    }

    /** ¿Aplica esta regla a un ingrediente y a una condición presente en el perfil? */
    public boolean aplicaA(String inci, String condicionPerfil) {
        return ingrediente.equals(normalizar(inci, "inci"))
                && condicion.equals(normalizar(condicionPerfil, "condicion"));
    }

    private static String normalizar(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return s.trim().toLowerCase();
    }

    public Long getId() { return id; }
    public String getIngrediente() { return ingrediente; }
    public String getCondicion() { return condicion; }
    public Severidad getSeveridad() { return severidad; }
    public String getMotivo() { return motivo; }
}
