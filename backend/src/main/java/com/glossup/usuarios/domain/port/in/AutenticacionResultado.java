package com.glossup.usuarios.domain.port.in;

import com.glossup.usuarios.domain.model.Usuario;

public record AutenticacionResultado(String token, Usuario usuario) {}
