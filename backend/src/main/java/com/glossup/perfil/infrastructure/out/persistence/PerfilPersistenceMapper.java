package com.glossup.perfil.infrastructure.out.persistence;

import com.glossup.perfil.domain.model.Ph;
import com.glossup.perfil.domain.model.PerfilDermatologico;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 * Traductor entre el agregado de dominio {@link PerfilDermatologico} y la entidad JPA
 * {@link PerfilDermatologicoJpaEntity}. Aísla al dominio de la tecnología de persistencia.
 */
final class PerfilPersistenceMapper {

    private PerfilPersistenceMapper() {
    }

    static PerfilDermatologicoJpaEntity aEntidad(PerfilDermatologico perfil) {
        PerfilDermatologicoJpaEntity e = new PerfilDermatologicoJpaEntity();
        e.setId(perfil.getId());
        e.setUsuarioId(perfil.getUsuarioId());
        e.setPh(perfil.getPh() == null ? null : BigDecimal.valueOf(perfil.getPh().valor()));
        e.setTipoPiel(perfil.getTipoPiel());
        e.setTipoCabello(perfil.getTipoCabello());
        e.setAfecciones(new LinkedHashSet<>(perfil.getAfecciones()));
        e.setAlergias(new LinkedHashSet<>(perfil.getAlergias()));
        e.setActualizadoEn(perfil.getActualizadoEn());
        return e;
    }

    static PerfilDermatologico aDominio(PerfilDermatologicoJpaEntity e) {
        Ph ph = e.getPh() == null ? null : new Ph(e.getPh().doubleValue());
        return new PerfilDermatologico(
                e.getId(),
                e.getUsuarioId(),
                ph,
                e.getTipoPiel(),
                e.getTipoCabello(),
                new LinkedHashSet<>(e.getAfecciones()),
                new LinkedHashSet<>(e.getAlergias()),
                e.getActualizadoEn());
    }
}
