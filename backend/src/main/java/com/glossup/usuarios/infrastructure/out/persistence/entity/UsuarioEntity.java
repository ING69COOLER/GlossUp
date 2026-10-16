package com.glossup.usuarios.infrastructure.out.persistence.entity;

import com.glossup.usuarios.domain.model.Rol;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    private Long id;
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;
    private String nombre;
    private boolean activo;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<RolEntity> roles = new HashSet<>();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public Set<RolEntity> getRoles() { return roles; }
}
