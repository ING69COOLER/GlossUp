package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    private Producto productoBase() {
        return Producto.registrar(1L, "Serum Vitamina C", "GlossLab", "serum",
                Dinero.de(50000), 10, 5.0, "INVIMA-123");
    }

    @Test
    void no_se_puede_publicar_sin_ficha_inci() {
        Producto p = productoBase();
        assertThrows(ReglaNegocioException.class, p::publicar);
    }

    @Test
    void se_publica_con_al_menos_un_ingrediente() {
        Producto p = productoBase();
        p.agregarIngrediente(ComposicionProducto.de(Ingrediente.nuevo(new Inci("Ascorbic Acid"), null)));
        p.publicar();
        assertTrue(p.isActivo());
    }

    @Test
    void reducir_stock_mas_del_disponible_falla() {
        Producto p = productoBase();
        assertThrows(ReglaNegocioException.class, () -> p.reducirStock(999));
    }

    @Test
    void precio_negativo_no_es_valido() {
        assertThrows(ReglaNegocioException.class, () -> Dinero.de(-1));
    }
}
