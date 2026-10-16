package com.glossup.catalogo.domain.port.in;

import com.glossup.catalogo.domain.model.Producto;

/**
 * Puerto de entrada: caso de uso para registrar un nuevo producto en el catálogo
 * con sus imágenes y ficha INCI completa.
 */
public interface RegistrarProductoUseCase {

    /**
     * Registra un producto en el catálogo verificando sus invariantes de negocio.
     *
     * @param command datos del producto, imágenes y ficha INCI.
     * @return el agregado {@link Producto} persistido.
     */
    Producto registrar(RegistrarProductoCommand command);
}
