package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    private ComposicionProducto ingredienteEjemplo(String inci, Double concentracion) {
        return new ComposicionProducto(
                Ingrediente.nuevo(new Inci(inci), "Descripción de " + inci),
                concentracion
        );
    }

    @Test
    void debeRegistrarProductoConFichaInciEImagenesCorrectamente() {
        List<ImagenProducto> imagenes = List.of(
                new ImagenProducto("https://cdn.glossup.co/img1.png"),
                new ImagenProducto("https://cdn.glossup.co/img2.png")
        );
        List<ComposicionProducto> ingredientes = List.of(
                ingredienteEjemplo("Ascorbic Acid", 15.0),
                ingredienteEjemplo("Hyaluronic Acid", 2.0)
        );

        Producto p = Producto.registrar(
                1L, "Serum Vitamina C", "GlossLab", "serum",
                Dinero.de(50000), 10, 3.5, "INVIMA-123",
                imagenes, ingredientes
        );

        assertNotNull(p);
        assertTrue(p.isActivo(), "El producto debe nacer activo al cumplir RNF-04 con ficha INCI");
        assertEquals(2, p.getImagenes().size());
        assertEquals(2, p.getComposicion().size());
        assertEquals("Serum Vitamina C", p.getNombre());
        assertEquals(50000.0, p.getPrecio().monto().doubleValue());
    }

    @Test
    void debeLanzarExcepcionAlRegistrarSinIngredientes() {
        List<ImagenProducto> imagenes = List.of(new ImagenProducto("https://cdn.glossup.co/img.png"));

        assertThrows(ReglaNegocioException.class, () ->
                Producto.registrar(
                        1L, "Crema Hidratante", "GlossLab", "crema",
                        Dinero.de(35000), 5, 5.5, "INVIMA-456",
                        imagenes, List.of()
                ), "Debe exigir al menos un ingrediente INCI"
        );
    }

    @Test
    void debeLanzarExcepcionAlRegistrarConListaIngredientesNula() {
        assertThrows(ReglaNegocioException.class, () ->
                Producto.registrar(
                        1L, "Tónico Facial", "GlossLab", "tonico",
                        Dinero.de(25000), 10, 5.0, "INVIMA-789",
                        List.of(), null
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlAgregarIngredienteConInciDuplicado() {
        List<ComposicionProducto> ingredientes = List.of(
                ingredienteEjemplo("Niacinamide", 10.0)
        );
        Producto p = Producto.registrar(
                1L, "Serum Niacinamida", "GlossLab", "serum",
                Dinero.de(45000), 20, 6.0, "INVIMA-999",
                List.of(), ingredientes
        );

        assertThrows(ReglaNegocioException.class, () ->
                p.agregarIngrediente(ingredienteEjemplo("Niacinamide", 5.0))
        );
    }

    @Test
    void debeLanzarExcepcionSiNombreEsInvalido() {
        List<ComposicionProducto> ingredientes = List.of(ingredienteEjemplo("Aqua", null));

        assertThrows(ReglaNegocioException.class, () ->
                Producto.registrar(
                        1L, "", "Marca", "cat",
                        Dinero.de(10000), 5, null, null,
                        List.of(), ingredientes
                )
        );
    }

    @Test
    void debeLanzarExcepcionSiStockEsNegativo() {
        List<ComposicionProducto> ingredientes = List.of(ingredienteEjemplo("Aqua", null));

        assertThrows(ReglaNegocioException.class, () ->
                Producto.registrar(
                        1L, "Gel", "Marca", "cat",
                        Dinero.de(10000), -5, null, null,
                        List.of(), ingredientes
                )
        );
    }

    @Test
    void precioNegativoNoEsValido() {
        assertThrows(ReglaNegocioException.class, () -> Dinero.de(-1));
    }

    @Test
    void reducirStockMasDelDisponibleFalla() {
        Producto p = Producto.registrar(
                1L, "Serum Vitamina C", "GlossLab", "serum",
                Dinero.de(50000), 10, 5.0, "INVIMA-123",
                List.of(), List.of(ingredienteEjemplo("Ascorbic Acid", null))
        );
        assertThrows(ReglaNegocioException.class, () -> p.reducirStock(999));
    }

    @Test
    void reducirYReponerStockExitoso() {
        Producto p = Producto.registrar(
                1L, "Serum", "GlossLab", "serum",
                Dinero.de(50000), 10, 5.0, "INVIMA-123",
                List.of(), List.of(ingredienteEjemplo("Ascorbic Acid", null))
        );
        p.reducirStock(4);
        assertEquals(6, p.getStock());

        p.reponerStock(10);
        assertEquals(16, p.getStock());
    }
}
