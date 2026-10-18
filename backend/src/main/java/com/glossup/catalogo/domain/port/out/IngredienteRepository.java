package com.glossup.catalogo.domain.port.out;

import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Inci;

/** Puerto de salida: ontología de ingredientes (INCI) compartida por los productos. */
public interface IngredienteRepository {

    /**
     * Devuelve el ingrediente con ese INCI, creándolo si aún no existe (evita duplicados).
     * Si se crea, guarda {@code funcion} como su descripción en la ontología; si ya existía,
     * conserva la que tenga.
     */
    Ingrediente obtenerOCrear(Inci inci, String funcion);
}
