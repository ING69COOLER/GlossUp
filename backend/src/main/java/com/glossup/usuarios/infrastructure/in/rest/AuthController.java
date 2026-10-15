package com.glossup.usuarios.infrastructure.in.rest;

import com.glossup.shared.exception.NoAutorizadoException;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.in.AutenticarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.ResultadoAutenticacion;
import com.glossup.usuarios.infrastructure.in.rest.dto.AuthResponse;
import com.glossup.usuarios.infrastructure.in.rest.dto.LoginRequest;
import com.glossup.usuarios.infrastructure.in.rest.dto.RegistrarRequest;
import com.glossup.usuarios.infrastructure.in.rest.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST de autenticación (módulo usuarios).
 *
 * <p>Rutas públicas bajo {@code /api/auth/**} (permitidas en SecurityConfig). El login emite un JWT
 * cuyo subject es el id del usuario y que incluye sus roles; con él, el frontend decide a cuál de
 * los tres paneles (CLIENTE, VENDEDOR, ADMIN) debe entrar.</p>
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, login y usuario actual (módulo usuarios)")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuario;
    private final AutenticarUsuarioUseCase autenticarUsuario;

    public AuthController(RegistrarUsuarioUseCase registrarUsuario,
                          AutenticarUsuarioUseCase autenticarUsuario) {
        this.registrarUsuario = registrarUsuario;
        this.autenticarUsuario = autenticarUsuario;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra un usuario nuevo")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarRequest request) {
        Usuario creado = registrarUsuario.registrar(request.aComando());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(creado));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y devuelve un token JWT con el rol/panel del usuario")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        ResultadoAutenticacion resultado = autenticarUsuario.autenticar(request.aComando());
        return AuthResponse.desde(resultado);
    }

    @GetMapping("/me")
    @Operation(summary = "Devuelve el usuario autenticado (a partir del token)")
    public UsuarioResponse yo(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new NoAutorizadoException("No hay sesión activa");
        }
        Long usuarioId = Long.valueOf(authentication.getName());
        return UsuarioResponse.desde(autenticarUsuario.consultarPorId(usuarioId));
    }
}
