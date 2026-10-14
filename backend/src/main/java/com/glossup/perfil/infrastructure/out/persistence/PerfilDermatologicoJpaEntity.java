package com.glossup.perfil.infrastructure.out.persistence;

import com.glossup.perfil.domain.model.TipoPiel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entidad JPA que mapea la tabla {@code perfil_dermatologico} (y sus tablas de colección
 * {@code perfil_afeccion}/{@code perfil_alergia}).
 *
 * <p>Es un detalle del adaptador de persistencia: existe para traducir hacia/desde el agregado de
 * dominio {@link com.glossup.perfil.domain.model.PerfilDermatologico}, que se mantiene libre de
 * anotaciones de framework.</p>
 */
@Entity
@Table(name = "perfil_dermatologico")
public class PerfilDermatologicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true, updatable = false)
    private Long usuarioId;

    @Column(name = "ph", precision = 3, scale = 1)
    private BigDecimal ph;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_piel", length = 20)
    private TipoPiel tipoPiel;

    @Column(name = "tipo_cabello", length = 30)
    private String tipoCabello;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "perfil_afeccion", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "nombre")
    private Set<String> afecciones = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "perfil_alergia", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "nombre")
    private Set<String> alergias = new LinkedHashSet<>();

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    protected PerfilDermatologicoJpaEntity() {
        // requerido por JPA
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getPh() { return ph; }
    public void setPh(BigDecimal ph) { this.ph = ph; }

    public TipoPiel getTipoPiel() { return tipoPiel; }
    public void setTipoPiel(TipoPiel tipoPiel) { this.tipoPiel = tipoPiel; }

    public String getTipoCabello() { return tipoCabello; }
    public void setTipoCabello(String tipoCabello) { this.tipoCabello = tipoCabello; }

    public Set<String> getAfecciones() { return afecciones; }
    public void setAfecciones(Set<String> afecciones) { this.afecciones = afecciones; }

    public Set<String> getAlergias() { return alergias; }
    public void setAlergias(Set<String> alergias) { this.alergias = alergias; }

    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
