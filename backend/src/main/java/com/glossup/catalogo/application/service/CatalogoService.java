package com.glossup.catalogo.application.service;

import com.glossup.catalogo.domain.model.ComposicionProducto;
import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.CrearProductoComando;
import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import com.glossup.catalogo.domain.port.in.EditarProductoUseCase;
import com.glossup.catalogo.domain.port.in.GestionCatalogoUseCase;
import com.glossup.catalogo.domain.port.out.IngredienteRepository;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación del catálogo. Orquesta la gestión de productos del vendedor (HU3: crear,
 * stock, ficha INCI, publicación) y la edición de datos comerciales (HU4). El dominio ({@link Producto})
 * guarda las invariantes; aquí solo se resuelve la propiedad del producto y se persiste.
 */
@Service
public class CatalogoService implements EditarProductoUseCase, GestionCatalogoUseCase {

    private final ProductoRepository repositorio;
    private final IngredienteRepository ingredientes;

    public CatalogoService(ProductoRepository repositorio, IngredienteRepository ingredientes) {
        this.repositorio = repositorio;
        this.ingredientes = ingredientes;
    }

    @Override
    @Transactional
    public Producto editar(EditarProductoComando c) {
        Producto producto = cargarDelVendedor(c.productoId(), c.vendedorId());
        producto.actualizarDatos(c.nombre(), c.marca(), c.categoria(), Dinero.de(c.precio()),
                c.ph(), c.registroInvima(), c.imagenUrl());
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional
    public Producto crear(CrearProductoComando c) {
        Producto producto = Producto.registrar(c.vendedorId(), c.nombre(), c.marca(), c.categoria(),
                Dinero.de(c.precio()), c.stock(), c.ph(), c.registroInvima(), c.imagenUrl());
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarDelVendedor(Long vendedorId) {
        return repositorio.buscarPorVendedor(vendedorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtener(Long productoId, Long vendedorId) {
        return cargarDelVendedor(productoId, vendedorId);
    }

    @Override
    @Transactional
    public Producto actualizarStock(Long productoId, Long vendedorId, int stock) {
        Producto producto = cargarDelVendedor(productoId, vendedorId);
        producto.fijarStock(stock);
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional
    public Producto agregarIngrediente(Long productoId, Long vendedorId, String nombreInci, Double concentracion, String funcion) {
        Producto producto = cargarDelVendedor(productoId, vendedorId);
        Ingrediente ingrediente = ingredientes.obtenerOCrear(new Inci(nombreInci), funcion);
        producto.agregarIngrediente(new ComposicionProducto(ingrediente, concentracion));
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional
    public Producto quitarIngrediente(Long productoId, Long vendedorId, String nombreInci) {
        Producto producto = cargarDelVendedor(productoId, vendedorId);
        producto.quitarIngrediente(new Inci(nombreInci));
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional
    public Producto publicar(Long productoId, Long vendedorId) {
        Producto producto = cargarDelVendedor(productoId, vendedorId);
        producto.publicar();
        return repositorio.guardar(producto);
    }

    @Override
    @Transactional
    public Producto despublicar(Long productoId, Long vendedorId) {
        Producto producto = cargarDelVendedor(productoId, vendedorId);
        producto.despublicar();
        return repositorio.guardar(producto);
    }

    /** Carga el producto solo si pertenece al vendedor; si no, 404 (no revela existencia ajena). */
    private Producto cargarDelVendedor(Long productoId, Long vendedorId) {
        return repositorio.buscarPorId(productoId)
                .filter(p -> vendedorId.equals(p.getVendedorId()))
                .orElseThrow(() -> new NotFoundException("Producto " + productoId + " no encontrado"));
    }
}
