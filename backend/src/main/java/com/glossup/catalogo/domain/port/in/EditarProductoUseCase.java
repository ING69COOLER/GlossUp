package com.glossup.catalogo.domain.port.in;

import com.glossup.catalogo.domain.model.Producto;

/** Puerto de entrada: edición de un producto por su vendedor (HU4). */
public interface EditarProductoUseCase {

    /**
     * Actualiza los datos comerciales del producto.
     *
     * @throws com.glossup.shared.exception.NotFoundException si el producto no existe o no es del vendedor
     * @throws com.glossup.shared.domain.DatoInvalidoException si algún dato rompe una invariante (nombre, precio)
     */
    Producto editar(EditarProductoComando comando);
}
