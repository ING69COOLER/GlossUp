package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

/**
 * Objeto de valor: hipervínculo/URL de una imagen del producto en el catálogo.
 *
 * <p>Invariantes:</p>
 * <ul>
 *   <li>La URL no puede ser nula ni estar en blanco.</li>
 *   <li>Se recorta el espacio en blanco al normalizar.</li>
 * </ul>
 */
public record ImagenProducto(String url) {

    public ImagenProducto {
        if (url == null || url.isBlank()) {
            throw new ReglaNegocioException("La URL de la imagen no puede estar vacía");
        }
        url = url.trim();
    }
}
