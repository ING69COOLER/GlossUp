package com.glossup.catalogo.application.service;

import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import com.glossup.catalogo.domain.port.out.IngredienteRepository;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** HU4: edición de producto con un repositorio falso en memoria. */
class CatalogoServiceTest {

    private static class RepoEnMemoria implements ProductoRepository {
        final Map<Long, Producto> datos = new HashMap<>();
        @Override public Producto guardar(Producto p) { datos.put(p.getId(), p); return p; }
        @Override public Optional<Producto> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
        @Override public List<Producto> buscarPorVendedor(Long vendedorId) {
            return datos.values().stream().filter(p -> vendedorId.equals(p.getVendedorId())).toList();
        }
    }

    /** Ontología falsa: asigna un id secuencial a cada INCI nuevo. */
    private static class IngredientesEnMemoria implements IngredienteRepository {
        final Map<String, Ingrediente> datos = new HashMap<>();
        @Override public Ingrediente obtenerOCrear(Inci inci, String funcion) {
            return datos.computeIfAbsent(inci.nombre(),
                    n -> new Ingrediente((long) (datos.size() + 1), inci, funcion));
        }
    }

    private RepoEnMemoria repo;
    private CatalogoService service;

    @BeforeEach
    void setUp() {
        repo = new RepoEnMemoria();
        // producto id=100 del vendedor 7
        repo.datos.put(100L, new Producto(100L, 7L, "Serum", "GlossLab", "serum",
                Dinero.de(50000), 10, 5.0, "INVIMA-1", null, true, new ArrayList<>(), java.time.OffsetDateTime.now()));
        service = new CatalogoService(repo, new IngredientesEnMemoria());
    }

    private EditarProductoComando cmd(Long productoId, Long vendedorId) {
        return new EditarProductoComando(productoId, vendedorId, "Serum Pro", "GlossLab", "serum", 60000, 5.5, "INVIMA-2", null);
    }

    @Test
    void el_vendedor_edita_su_producto() {
        Producto p = service.editar(cmd(100L, 7L));
        assertEquals("Serum Pro", p.getNombre());
        assertEquals(Dinero.de(60000), p.getPrecio());
        assertEquals(5.5, p.getPh());
    }

    @Test
    void no_puede_editar_el_producto_de_otro_vendedor() {
        assertThrows(NotFoundException.class, () -> service.editar(cmd(100L, 999L)));
    }

    @Test
    void producto_inexistente_es_404() {
        assertThrows(NotFoundException.class, () -> service.editar(cmd(404L, 7L)));
    }

    @Test
    void precio_negativo_lo_rechaza_el_dominio() {
        EditarProductoComando malo = new EditarProductoComando(100L, 7L, "Serum", null, null, -1, null, null, null);
        assertThrows(com.glossup.shared.domain.DatoInvalidoException.class, () -> service.editar(malo));
    }

    // ---- HU3: gestión del catálogo del vendedor ----

    private com.glossup.catalogo.domain.port.in.CrearProductoComando nuevo(Long vendedorId) {
        return new com.glossup.catalogo.domain.port.in.CrearProductoComando(
                vendedorId, "Crema SPF", "GlossLab", "crema", 42000, 5, 5.5, "INVIMA-9", "http://img/1.png");
    }

    @Test
    void crear_registra_un_borrador_del_vendedor() {
        Producto p = service.crear(nuevo(7L));
        assertEquals(7L, p.getVendedorId());
        assertFalse(p.isActivo(), "un producto nuevo nace como borrador");
        assertEquals("http://img/1.png", p.getImagenUrl());
    }

    @Test
    void publicar_requiere_ficha_inci() {
        Producto p = service.crear(nuevo(7L));
        assertThrows(com.glossup.shared.domain.OperacionNoPermitidaException.class,
                () -> service.publicar(p.getId(), 7L));

        service.agregarIngrediente(p.getId(), 7L, "Niacinamide", 10.0, "Activo");
        Producto publicado = service.publicar(p.getId(), 7L);
        assertTrue(publicado.isActivo());
        assertEquals(1, publicado.getComposicion().size());
    }

    @Test
    void actualizar_stock_fija_la_disponibilidad() {
        assertEquals(0, service.actualizarStock(100L, 7L, 0).getStock());
    }

    @Test
    void no_puede_tocar_el_producto_de_otro_vendedor() {
        assertThrows(NotFoundException.class, () -> service.actualizarStock(100L, 999L, 3));
    }
}
