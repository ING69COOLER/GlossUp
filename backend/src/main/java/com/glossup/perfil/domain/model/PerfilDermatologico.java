package com.glossup.perfil.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad de dominio: perfil dermatológico del usuario.
 *
 * <p>Alimenta al motor de compatibilidad (proceso "Gestión del perfil dermatológico").
 * Soporta la gestión dinámica de alergias y afecciones (HU2: agregar/quitar).</p>
 *
 * <p>Invariantes:</p>
 * <ul>
 *   <li>Siempre está asociado a un usuario ({@code usuarioId}).</li>
 *   <li>pH, tipo de piel y cabello son opcionales, pero si se dan deben ser válidos.</li>
 *   <li>Afecciones y alergias se guardan normalizadas (sin duplicados ni vacíos).</li>
 * </ul>
 */
public class PerfilDermatologico {

    private final Long id;
    private final Long usuarioId;
    private Ph ph;
    private TipoPiel tipoPiel;
    private String tipoCabello;
    private final Set<String> afecciones;
    private final Set<String> alergias;
    private OffsetDateTime actualizadoEn;

    public PerfilDermatologico(Long id, Long usuarioId, Ph ph, TipoPiel tipoPiel,
                               String tipoCabello, Set<String> afecciones, Set<String> alergias,
                               OffsetDateTime actualizadoEn) {
        this.id = id;
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId requerido");
        this.ph = ph;
        this.tipoPiel = tipoPiel;
        this.tipoCabello = normalizarOpcional(tipoCabello);
        this.afecciones = new LinkedHashSet<>();
        this.alergias = new LinkedHashSet<>();
        if (afecciones != null) afecciones.forEach(this::agregarAfeccion);
        if (alergias != null) alergias.forEach(this::agregarAlergia);
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : OffsetDateTime.now();
    }

    /** Fábrica para crear el perfil inicial de un usuario (HU1). */
    public static PerfilDermatologico crear(Long usuarioId, Ph ph, TipoPiel tipoPiel, String tipoCabello) {
        return new PerfilDermatologico(null, usuarioId, ph, tipoPiel, tipoCabello,
                Set.of(), Set.of(), OffsetDateTime.now());
    }

    // ---- Reglas de negocio / comportamiento (HU2) ----

    public void agregarAlergia(String alergia) {
        alergias.add(normalizarObligatorio(alergia, "alergia"));
        tocar();
    }

    public void quitarAlergia(String alergia) {
        alergias.remove(normalizar(alergia));
        tocar();
    }

    public void agregarAfeccion(String afeccion) {
        afecciones.add(normalizarObligatorio(afeccion, "afección"));
        tocar();
    }

    public void quitarAfeccion(String afeccion) {
        afecciones.remove(normalizar(afeccion));
        tocar();
    }

    public void actualizarBiotipo(Ph ph, TipoPiel tipoPiel, String tipoCabello) {
        this.ph = ph;
        this.tipoPiel = tipoPiel;
        this.tipoCabello = normalizarOpcional(tipoCabello);
        tocar();
    }

    /** ¿El usuario es alérgico a este ingrediente (por nombre INCI)? Útil para el motor de reglas. */
    public boolean esAlergicoA(String nombreInci) {
        return alergias.contains(normalizar(nombreInci));
    }

    public boolean tieneAfeccion(String afeccion) {
        return afecciones.contains(normalizar(afeccion));
    }

    // ---- Utilidades de normalización ----

    private void tocar() {
        this.actualizadoEn = OffsetDateTime.now();
    }

    private static String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static String normalizarOpcional(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static String normalizarObligatorio(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new ReglaNegocioException("La " + campo + " no puede estar vacía");
        }
        return s.trim().toLowerCase();
    }

    // ---- Accesores ----

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Ph getPh() { return ph; }
    public TipoPiel getTipoPiel() { return tipoPiel; }
    public String getTipoCabello() { return tipoCabello; }
    public Set<String> getAfecciones() { return Set.copyOf(afecciones); }
    public Set<String> getAlergias() { return Set.copyOf(alergias); }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
}
