package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ImagenProductoTest {

    @Test
    void debeCrearImagenConUrlValida() {
        String url = "https://cdn.glossup.co/images/serum.webp";
        ImagenProducto imagen = new ImagenProducto(url);

        assertEquals(url, imagen.url());
    }

    @Test
    void debeRecortarEspaciosEnBlancoDeLaUrl() {
        String urlConEspacios = "   https://cdn.glossup.co/images/serum.webp   ";
        ImagenProducto imagen = new ImagenProducto(urlConEspacios);

        assertEquals("https://cdn.glossup.co/images/serum.webp", imagen.url());
    }

    @Test
    void debeLanzarExcepcionSiUrlEsNula() {
        assertThrows(ReglaNegocioException.class, () -> new ImagenProducto(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void debeLanzarExcepcionSiUrlEstaVaciaOEnBlanco(String urlInvalida) {
        assertThrows(ReglaNegocioException.class, () -> new ImagenProducto(urlInvalida));
    }
}
