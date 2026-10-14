package com.glossup.perfil.infrastructure.in.rest.dto;

import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.model.TipoPiel;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Representación del perfil dermatológico devuelta por la API (no expone el modelo de dominio).
 */
public record PerfilResponse(
        Long id,
        Long usuarioId,
        Double ph,
        TipoPiel tipoPiel,
        String tipoCabello,
        Set<String> afecciones,
        Set<String> alergias,
        OffsetDateTime actualizadoEn) {

    public static PerfilResponse desde(PerfilDermatologico p) {
        return new PerfilResponse(
                p.getId(),
                p.getUsuarioId(),
                p.getPh() == null ? null : p.getPh().valor(),
                p.getTipoPiel(),
                p.getTipoCabello(),
                p.getAfecciones(),
                p.getAlergias(),
                p.getActualizadoEn());
    }
}
