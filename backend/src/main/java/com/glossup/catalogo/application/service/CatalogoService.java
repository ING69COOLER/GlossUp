package com.glossup.catalogo.application.service;

import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.EditarProductoComando;
import com.glossup.catalogo.domain.port.in.EditarProductoUseCase;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Servicio de aplicación del catálogo. HU4: el vendedor edita los datos de su producto. */
@Service
public class CatalogoService implements EditarProductoUseCase {

    private final ProductoRepository repositorio;

    public CatalogoService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public Producto editar(EditarProductoComando c) {
        Producto producto = repositorio.buscarPorId(c.productoId())
                .filter(p -> c.vendedorId().equals(p.getVendedorId()))
                .orElseThrow(() -> new NotFoundException("Producto " + c.productoId() + " no encontrado"));

        producto.actualizarDatos(c.nombre(), c.marca(), c.categoria(), Dinero.de(c.precio()), c.ph(), c.registroInvima());
        return repositorio.guardar(producto);
    }
}
