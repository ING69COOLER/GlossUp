package com.glossup.catalogo.domain.port.out;

import com.glossup.catalogo.domain.model.Producto;

import java.util.Optional;

/** Puerto de salida del módulo de catálogo: persistencia de productos. */
public interface ProductoRepository {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);
}
