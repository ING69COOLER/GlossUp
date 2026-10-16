package com.glossup.usuarios.infrastructure.in.rest;

import com.glossup.usuarios.domain.port.in.AutenticarUsuarioCommand;
import com.glossup.usuarios.domain.port.in.AutenticacionResultado;
import com.glossup.usuarios.domain.port.in.AutenticarUsuarioUseCase;
import com.glossup.usuarios.infrastructure.in.rest.dto.LoginRequest;
import com.glossup.usuarios.infrastructure.in.rest.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AutenticacionResultado resultado = autenticarUsuarioUseCase.autenticar(
                new AutenticarUsuarioCommand(request.email(), request.password()));
        Set<String> roles = resultado.usuario().getRoles().stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return ResponseEntity.ok(new LoginResponse(resultado.token(), resultado.usuario().getId(),
                resultado.usuario().getEmail(), roles));
    }
}
