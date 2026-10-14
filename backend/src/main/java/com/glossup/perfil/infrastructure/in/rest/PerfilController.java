package com.glossup.perfil.infrastructure.in.rest;

import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.port.in.GestionarPerfilDermatologicoUseCase;
import com.glossup.perfil.infrastructure.in.rest.dto.CrearPerfilRequest;
import com.glossup.perfil.infrastructure.in.rest.dto.PerfilResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.net.URI;

/**
 * Adaptador de entrada REST del módulo de perfil (HU1 — Sprint 2).
 *
 * <p>Delega en el puerto de entrada {@link GestionarPerfilDermatologicoUseCase}: no contiene lógica
 * de negocio, solo traduce HTTP ⇄ caso de uso. El {@code usuarioId} se toma del usuario autenticado
 * (subject del JWT), de modo que cada cliente solo gestiona su propio perfil (seguridad RNF-11).</p>
 */
@RestController
@RequestMapping("/api/perfil")
@Tag(name = "Perfil dermatológico", description = "Registro y consulta del perfil del usuario (HU1)")
@SecurityRequirement(name = "bearer-jwt")
public class PerfilController {

    private final GestionarPerfilDermatologicoUseCase gestionarPerfil;

    public PerfilController(GestionarPerfilDermatologicoUseCase gestionarPerfil) {
        this.gestionarPerfil = gestionarPerfil;
    }

    @PostMapping
    @Operation(summary = "Crea el perfil dermatológico del usuario autenticado")
    public ResponseEntity<PerfilResponse> crear(@Valid @RequestBody CrearPerfilRequest request,
                                                 Authentication authentication) {
        Long usuarioId = usuarioIdDe(authentication);
        PerfilDermatologico perfil = gestionarPerfil.crear(request.aComando(usuarioId));

        // Recurso singleton del usuario autenticado: siempre consultable en /api/perfil/me.
        return ResponseEntity.created(URI.create("/api/perfil/me")).body(PerfilResponse.desde(perfil));
    }

    @GetMapping("/me")
    @Operation(summary = "Devuelve el perfil dermatológico del usuario autenticado")
    public PerfilResponse consultarPropio(Authentication authentication) {
        return PerfilResponse.desde(gestionarPerfil.consultarPorUsuario(usuarioIdDe(authentication)));
    }

    /** El identificador del usuario viaja en el subject del JWT. */
    private static Long usuarioIdDe(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
