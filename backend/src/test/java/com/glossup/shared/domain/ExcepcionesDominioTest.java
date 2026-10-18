package com.glossup.shared.domain;

import com.glossup.catalogo.domain.model.ComposicionProducto;
import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Producto;
import com.glossup.recomendacion.domain.model.Pedido;
import com.glossup.recomendacion.domain.model.PedidoItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que cada regla de negocio lanza la excepción específica correcta y con el código de
 * error estable esperado (no solo "una ReglaNegocioException genérica").
 */
class ExcepcionesDominioTest {

    @Test
    void monto_negativo_lanza_DatoInvalido_con_codigo() {
        DatoInvalidoException ex = assertThrows(DatoInvalidoException.class, () -> Dinero.de(-1));
        assertEquals(CodigoError.MONTO_NEGATIVO, ex.getCodigo());
    }

    @Test
    void publicar_sin_inci_lanza_OperacionNoPermitida_con_codigo() {
        Producto p = Producto.registrar(1L, "Serum", "GlossLab", "serum", Dinero.de(50000), 10, 5.0, null, null);
        OperacionNoPermitidaException ex =
                assertThrows(OperacionNoPermitidaException.class, p::publicar);
        assertEquals(CodigoError.PRODUCTO_SIN_INCI, ex.getCodigo());
    }

    @Test
    void stock_insuficiente_lanza_excepcion_con_datos_estructurados() {
        Producto p = Producto.registrar(1L, "Serum", "GlossLab", "serum", Dinero.de(50000), 3, 5.0, null, null);
        StockInsuficienteException ex =
                assertThrows(StockInsuficienteException.class, () -> p.reducirStock(10));
        assertEquals(CodigoError.STOCK_INSUFICIENTE, ex.getCodigo());
        assertEquals(3, ex.getDisponible());
        assertEquals(10, ex.getSolicitado());
    }

    @Test
    void transicion_invalida_lanza_TransicionInvalida_con_codigo() {
        Pedido p = Pedido.crear(1L, List.of(new PedidoItem(100L, 1, Dinero.de(1000))));
        TransicionInvalidaException ex =
                assertThrows(TransicionInvalidaException.class, p::marcarEnviado);
        assertEquals(CodigoError.TRANSICION_INVALIDA, ex.getCodigo());
    }

    @Test
    void todas_las_excepciones_de_dominio_comparten_la_raiz() {
        // Permite que la capa web las trate de forma uniforme (un solo @ExceptionHandler base).
        assertThrows(ReglaNegocioException.class,
                () -> new Producto(1L, 1L, "Serum", null, null, Dinero.de(1000), -5, null, null, null,
                        false, List.of(ComposicionProducto.de(Ingrediente.nuevo(new Inci("Aqua"), null))), null));
    }
}
