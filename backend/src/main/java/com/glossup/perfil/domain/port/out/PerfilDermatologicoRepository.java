package com.glossup.perfil.domain.port.out;

import com.glossup.perfil.domain.model.PerfilDermatologico;

import java.util.Optional;

/**
 * Puerto de salida del módulo de perfil: contrato de persistencia que el dominio necesita, sin
 * conocer la tecnología que lo implementa (JPA/PostgreSQL es un detalle del adaptador en
 * {@code infrastructure/out}). Permite probar la capa de aplicación con una implementación en
 * memoria.
 */
public interface PerfilDermatologicoRepository {

    /** Crea o actualiza el perfil y devuelve el agregado con su id asignado. */
    PerfilDermatologico guardar(PerfilDermatologico perfil);

    /** Busca el perfil de un usuario (relación 1:1). */
    Optional<PerfilDermatologico> buscarPorUsuarioId(Long usuarioId);

    /** ¿El usuario ya tiene un perfil? Usado para hacer cumplir la unicidad. */
    boolean existePorUsuarioId(Long usuarioId);
}
