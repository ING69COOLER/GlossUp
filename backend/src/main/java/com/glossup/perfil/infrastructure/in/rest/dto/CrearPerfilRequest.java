package com.glossup.perfil.infrastructure.in.rest.dto;

import com.glossup.perfil.domain.model.TipoPiel;
import com.glossup.perfil.domain.port.in.CrearPerfilComando;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Cuerpo de la petición para crear el perfil dermatológico (HU1).
 *
 * <p>La validación sintáctica (rango de pH, longitudes) se hace aquí con Bean Validation y produce
 * HTTP 400; las reglas de negocio las valida el dominio (HTTP 422). Todos los campos del biotipo
 * son opcionales: el perfil puede crearse y completarse luego.</p>
 */
public record CrearPerfilRequest(

        @DecimalMin(value = "0.0", message = "el pH mínimo es 0")
        @DecimalMax(value = "14.0", message = "el pH máximo es 14")
        Double ph,

        TipoPiel tipoPiel,

        @Size(max = 30, message = "el tipo de cabello admite máximo 30 caracteres")
        String tipoCabello,

        Set<@Size(max = 120) String> afecciones,

        Set<@Size(max = 120) String> alergias) {

    /** Convierte la petición en el comando del caso de uso, fijando el usuario autenticado. */
    public CrearPerfilComando aComando(Long usuarioId) {
        return new CrearPerfilComando(usuarioId, ph, tipoPiel, tipoCabello, afecciones, alergias);
    }
}
