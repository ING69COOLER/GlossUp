package com.glossup.compatibilidad.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de una evaluación de compatibilidad (producto–perfil o producto–producto).
 *
 * <p>Regla de negocio: se considera "no compatible" si existe al menos una alerta de severidad
 * ALTA (alerta roja). Las alertas BAJA/MEDIA informan sin bloquear.</p>
 */
public class ResultadoCompatibilidad {

    private final List<Alerta> alertas;

    public ResultadoCompatibilidad(List<Alerta> alertas) {
        this.alertas = new ArrayList<>(alertas == null ? List.of() : alertas);
    }

    public static ResultadoCompatibilidad vacio() {
        return new ResultadoCompatibilidad(new ArrayList<>());
    }

    public void agregar(Alerta alerta) {
        if (alerta != null) alertas.add(alerta);
    }

    /** Compatible si no hay ninguna alerta crítica (ALTA). */
    public boolean esCompatible() {
        return alertas.stream().noneMatch(Alerta::esCritica);
    }

    public boolean tieneAlertas() {
        return !alertas.isEmpty();
    }

    public List<Alerta> getAlertas() {
        return List.copyOf(alertas);
    }
}
