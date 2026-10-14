package com.glossup.perfil.domain.port.in;

import com.glossup.perfil.domain.model.PerfilDermatologico;

/**
 * Puerto de entrada del módulo de perfil: los casos de uso que ofrece al exterior.
 *
 * <p>Alcance de la HU1 (Sprint 2) — "Gestión del perfil dermatológico": el cliente autenticado
 * registra su perfil (biotipo + afecciones/alergias) y puede consultarlo. El adaptador REST
 * (infrastructure/in) invoca este puerto; la implementación vive en la capa de aplicación.</p>
 */
public interface GestionarPerfilDermatologicoUseCase {

    /**
     * Crea el perfil dermatológico del usuario.
     *
     * @throws com.glossup.shared.domain.OperacionNoPermitidaException si el usuario ya tiene perfil
     * @throws com.glossup.shared.domain.DatoInvalidoException si algún dato rompe una invariante
     */
    PerfilDermatologico crear(CrearPerfilComando comando);

    /**
     * Devuelve el perfil del usuario.
     *
     * @throws com.glossup.shared.exception.NotFoundException si el usuario no tiene perfil
     */
    PerfilDermatologico consultarPorUsuario(Long usuarioId);

    // ---- HU2: gestión dinámica del perfil (sobre un perfil ya existente) ----

    /**
     * Agrega una alergia (nombre INCI) al perfil del usuario. Idempotente: agregar una ya presente
     * no la duplica (el dominio normaliza).
     *
     * @throws com.glossup.shared.exception.NotFoundException si el usuario no tiene perfil
     * @throws com.glossup.shared.domain.DatoInvalidoException si el valor viene vacío
     */
    PerfilDermatologico agregarAlergia(Long usuarioId, String alergia);

    /** Quita una alergia del perfil (idempotente si no estaba). */
    PerfilDermatologico quitarAlergia(Long usuarioId, String alergia);

    /** Agrega una afección/condición dérmica al perfil. */
    PerfilDermatologico agregarAfeccion(Long usuarioId, String afeccion);

    /** Quita una afección del perfil (idempotente si no estaba). */
    PerfilDermatologico quitarAfeccion(Long usuarioId, String afeccion);

    /** Actualiza el biotipo (pH, tipo de piel, tipo de cabello) del perfil. */
    PerfilDermatologico actualizarBiotipo(ActualizarBiotipoComando comando);
}
