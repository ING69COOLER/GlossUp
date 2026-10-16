package com.glossup.usuarios.infrastructure.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class RolEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public String getNombre() { return nombre; }
}
