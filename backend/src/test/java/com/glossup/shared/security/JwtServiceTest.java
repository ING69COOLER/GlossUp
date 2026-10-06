package com.glossup.shared.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria pura (sin contexto de Spring ni base de datos) del servicio JWT.
 * Sirve de ejemplo del enfoque de pruebas del dominio (RNF-27).
 */
class JwtServiceTest {

    private static final String SECRET = "secreto-de-prueba-con-longitud-suficiente-32+";
    private final JwtService jwtService = new JwtService(SECRET, 3_600_000L);

    @Test
    void genera_y_valida_un_token_correctamente() {
        String token = jwtService.generarToken("ana@glossup.com", List.of("CLIENTE"));

        assertTrue(jwtService.esValido(token));
        assertEquals("ana@glossup.com", jwtService.extraerSubject(token));
        assertEquals(List.of("CLIENTE"), jwtService.extraerRoles(token));
    }

    @Test
    void un_token_manipulado_es_invalido() {
        String token = jwtService.generarToken("ana@glossup.com", List.of("CLIENTE"));
        assertFalse(jwtService.esValido(token + "xx"));
    }
}
