package com.glossup.catalogo.application.service;

import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** HU4: edición de producto con un repositorio falso en memoria. */
class CatalogoServiceTest {

    private static class RepoEnMemoria implements ProductoRepository {
        final Map<Long, Producto> datos = new HashMap<>();
        @Override public Producto guardar(Producto p) { datos.put(p.getId(), p); return p; }
        @Override public Optional<Producto> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
    }

    private RepoEnMemoria repo;
    private CatalogoService service;

    @BeforeEach
    void setUp() {
        repo = new RepoEnMemoria();
        // producto id=100 del vendedor 7
        repo.datos.put(100L, new Producto(100L, 7L, "Serum", "GlossLab", "serum",
                Dinero.de(50000), 10, 5.0, "INVIMA-1", true, java.util.List.of(), java.time.OffsetDateTime.now()));
        service = new CatalogoService(repo);
    }

    private EditarProductoComando cmd(Long productoId, Long vendedorId) {
        return new EditarProductoComando(productoId, vendedorId, "Serum Pro", "GlossLab", "serum", 60000, 5.5, "INVIMA-2");
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
        EditarProductoComando malo = new EditarProductoComando(100L, 7L, "Serum", null, null, -1, null, null);
        assertThrows(com.glossup.shared.domain.DatoInvalidoException.class, () -> service.editar(malo));
    }
}
