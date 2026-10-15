package com.glossup.usuarios.infrastructure.in.rest;

import com.glossup.shared.exception.GlobalExceptionHandler;
import com.glossup.shared.exception.NoAutorizadoException;
import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.in.AutenticarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.CredencialesComando;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioComando;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.ResultadoAutenticacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del adaptador REST de autenticación con MockMvc standalone (casos de uso simulados).
 */
class AuthControllerTest {

    private RegistrarUsuarioUseCase registrar;
    private AutenticarUsuarioUseCase autenticar;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registrar = mock(RegistrarUsuarioUseCase.class);
        autenticar = mock(AutenticarUsuarioUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(registrar, autenticar))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Usuario usuario(long id, String nombre, Set<Rol> roles) {
        return new Usuario(id, new Email("user@mail.com"), nombre, "hash", true, roles, OffsetDateTime.now());
    }

    @Test
    void register_devuelve_201_con_el_usuario() throws Exception {
        when(registrar.registrar(any(RegistrarUsuarioComando.class)))
                .thenReturn(usuario(5L, "Ana", EnumSet.of(Rol.CLIENTE)));

        mockMvc.perform(post("/api/auth/register").contentType("application/json")
                        .content("{\"email\":\"ana@mail.com\",\"nombre\":\"Ana\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(5))
                .andExpect(jsonPath("$.panel").value("CLIENTE"));
    }

    @Test
    void login_devuelve_token_y_panel_del_rol() throws Exception {
        when(autenticar.autenticar(any(CredencialesComando.class)))
                .thenReturn(new ResultadoAutenticacion("jwt-abc", usuario(9L, "Admin", EnumSet.of(Rol.ADMIN))));

        mockMvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"email\":\"admin@mail.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-abc"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.panel").value("ADMIN"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    void login_con_credenciales_invalidas_devuelve_401() throws Exception {
        when(autenticar.autenticar(any(CredencialesComando.class)))
                .thenThrow(new NoAutorizadoException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"email\":\"x@mail.com\",\"password\":\"mala1234\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTORIZADO"));
    }

    @Test
    void register_sin_password_devuelve_400() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType("application/json")
                        .content("{\"email\":\"ana@mail.com\",\"nombre\":\"Ana\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(registrar);
    }

    @Test
    void me_sin_token_devuelve_401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_con_token_devuelve_el_usuario() throws Exception {
        when(autenticar.consultarPorId(9L)).thenReturn(usuario(9L, "Admin", EnumSet.of(Rol.ADMIN)));
        Authentication auth = new UsernamePasswordAuthenticationToken("9", null, List.of());

        mockMvc.perform(get("/api/auth/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(9))
                .andExpect(jsonPath("$.panel").value("ADMIN"));
    }
}
