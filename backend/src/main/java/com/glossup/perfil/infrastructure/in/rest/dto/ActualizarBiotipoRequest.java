package com.glossup.perfil.infrastructure.in.rest.dto;

import com.glossup.perfil.domain.model.TipoPiel;
import com.glossup.perfil.domain.port.in.ActualizarBiotipoComando;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para actualizar el biotipo del perfil (HU2). Todos los campos son opcionales.
 */
public record ActualizarBiotipoRequest(

        @DecimalMin(value = "0.0", message = "el pH mínimo es 0")
        @DecimalMax(value = "14.0", message = "el pH máximo es 14")
        Double ph,

        TipoPiel tipoPiel,

        @Size(max = 30, message = "el tipo de cabello admite máximo 30 caracteres")
        String tipoCabello) {

    public ActualizarBiotipoComando aComando(Long usuarioId) {
        return new ActualizarBiotipoComando(usuarioId, ph, tipoPiel, tipoCabello);
    }
}
