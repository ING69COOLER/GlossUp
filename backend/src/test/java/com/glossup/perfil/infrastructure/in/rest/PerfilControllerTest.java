package com.glossup.perfil.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.model.Ph;
import com.glossup.perfil.domain.model.TipoPiel;
import com.glossup.perfil.domain.port.in.CrearPerfilComando;
import com.glossup.perfil.domain.port.in.GestionarPerfilDermatologicoUseCase;
import com.glossup.shared.domain.CodigoError;
import com.glossup.shared.domain.OperacionNoPermitidaException;
import com.glossup.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del adaptador REST de la HU1 con MockMvc en modo standalone: se aísla el controlador y su
 * @ControllerAdvice, inyectando un caso de uso simulado (Mockito) y un usuario autenticado vía
 * principal. No arranca Spring ni la base de datos.
 */
class PerfilControllerTest {

    private GestionarPerfilDermatologicoUseCase useCase;
    private MockMvc mockMvc;
    private final ObjectMapper json = new ObjectMapper();

    private final Authentication usuario7 =
            new UsernamePasswordAuthenticationToken("7", null, List.of());

    @BeforeEach
    void setUp() {
        useCase = mock(GestionarPerfilDermatologicoUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PerfilController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private PerfilDermatologico perfilDe(long usuarioId) {
        return new PerfilDermatologico(1L, usuarioId, new Ph(5.2), TipoPiel.MIXTA, "liso",
                Set.of("acné"), Set.of("parfum"), OffsetDateTime.now());
    }

    @Test
    void crear_devuelve_201_y_toma_el_usuario_del_token() throws Exception {
        when(useCase.crear(any(CrearPerfilComando.class))).thenReturn(perfilDe(7L));

        String body = """
                {"ph": 5.2, "tipoPiel": "MIXTA", "tipoCabello": "liso",
                 "afecciones": ["Acné"], "alergias": ["Parfum"]}""";

        mockMvc.perform(post("/api/perfil").principal(usuario7)
                        .contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/perfil/me"))
                .andExpect(jsonPath("$.usuarioId").value(7))
                .andExpect(jsonPath("$.tipoPiel").value("MIXTA"));

        // Verifica que el usuarioId salió del principal (7), no del cuerpo.
        var captor = org.mockito.ArgumentCaptor.forClass(CrearPerfilComando.class);
        verify(useCase).crear(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(7L, captor.getValue().usuarioId());
    }

    @Test
    void ph_fuera_de_rango_devuelve_400_por_validacion() throws Exception {
        mockMvc.perform(post("/api/perfil").principal(usuario7)
                        .contentType("application/json").content("{\"ph\": 99}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(useCase);
    }

    @Test
    void tipo_de_piel_invalido_devuelve_400() throws Exception {
        mockMvc.perform(post("/api/perfil").principal(usuario7)
                        .contentType("application/json").content("{\"tipoPiel\": \"PLATEADA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CUERPO_INVALIDO"));
        verifyNoInteractions(useCase);
    }

    @Test
    void perfil_duplicado_devuelve_422_con_codigo() throws Exception {
        when(useCase.crear(any(CrearPerfilComando.class)))
                .thenThrow(new OperacionNoPermitidaException(CodigoError.PERFIL_YA_EXISTE, "ya existe"));

        mockMvc.perform(post("/api/perfil").principal(usuario7)
                        .contentType("application/json").content("{\"tipoPiel\": \"SECA\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("PERFIL_YA_EXISTE"));
    }

    @Test
    void consultar_propio_devuelve_200() throws Exception {
        when(useCase.consultarPorUsuario(7L)).thenReturn(perfilDe(7L));

        mockMvc.perform(get("/api/perfil/me").principal(usuario7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(7))
                .andExpect(jsonPath("$.alergias[0]").value("parfum"));
    }
}
