package com.glossup.usuarios.infrastructure.out.security;

import com.glossup.usuarios.domain.port.out.CifradorDePassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa el puerto {@link CifradorDePassword} con el {@link PasswordEncoder}
 * de Spring Security (BCrypt, declarado en SecurityConfig).
 */
@Component
public class BCryptPasswordAdapter implements CifradorDePassword {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String cifrar(String passwordPlano) {
        return passwordEncoder.encode(passwordPlano);
    }

    @Override
    public boolean coincide(String passwordPlano, String hashAlmacenado) {
        return passwordEncoder.matches(passwordPlano, hashAlmacenado);
    }
}
