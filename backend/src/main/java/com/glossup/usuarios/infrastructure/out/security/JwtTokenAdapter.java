package com.glossup.usuarios.infrastructure.out.security;

import com.glossup.shared.security.JwtService;
import com.glossup.usuarios.domain.port.out.GeneradorDeToken;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador que implementa el puerto {@link GeneradorDeToken} delegando en el {@link JwtService}
 * transversal. El subject del token es el id del usuario (como contrato para el resto del sistema).
 */
@Component
public class JwtTokenAdapter implements GeneradorDeToken {

    private final JwtService jwtService;

    public JwtTokenAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generar(Long usuarioId, List<String> roles) {
        return jwtService.generarToken(String.valueOf(usuarioId), roles);
    }
}
