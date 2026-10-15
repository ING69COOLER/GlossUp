package com.glossup.catalogo.infrastructure.in.rest;

import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import com.glossup.catalogo.domain.port.in.EditarProductoUseCase;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.exception.GlobalExceptionHandler;
import com.glossup.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** HU4: adaptador REST con MockMvc standalone (caso de uso simulado). */
class ProductoControllerTest {

    private EditarProductoUseCase useCase;
    private MockMvc mockMvc;
    private final Authentication vendedor7 = new UsernamePasswordAuthenticationToken("7", null, List.of());

    @BeforeEach
    void setUp() {
        useCase = mock(EditarProductoUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductoController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static final String BODY =
            "{\"nombre\":\"Serum Pro\",\"marca\":\"GlossLab\",\"categoria\":\"serum\",\"precio\":60000,\"ph\":5.5}";

    @Test
    void editar_devuelve_200_y_toma_el_vendedor_del_token() throws Exception {
        when(useCase.editar(any(EditarProductoComando.class))).thenReturn(
                new Producto(100L, 7L, "Serum Pro", "GlossLab", "serum", Dinero.de(60000), 10, 5.5, "INVIMA-2",
                        true, List.of(), OffsetDateTime.now()));

        mockMvc.perform(put("/api/catalogo/productos/100").principal(vendedor7)
                        .contentType("application/json").content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Serum Pro"))
                .andExpect(jsonPath("$.precio").value(60000.0));

        var captor = org.mockito.ArgumentCaptor.forClass(EditarProductoComando.class);
        verify(useCase).editar(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(7L, captor.getValue().vendedorId());
        org.junit.jupiter.api.Assertions.assertEquals(100L, captor.getValue().productoId());
    }

    @Test
    void producto_inexistente_devuelve_404() throws Exception {
        when(useCase.editar(any())).thenThrow(new NotFoundException("no encontrado"));
        mockMvc.perform(put("/api/catalogo/productos/100").principal(vendedor7)
                        .contentType("application/json").content(BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void nombre_en_blanco_devuelve_400() throws Exception {
        mockMvc.perform(put("/api/catalogo/productos/100").principal(vendedor7)
                        .contentType("application/json").content("{\"nombre\":\"  \",\"precio\":1000}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(useCase);
    }
}
