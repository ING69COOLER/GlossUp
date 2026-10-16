package com.glossup.catalogo.domain.port.out;

import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;

import java.util.Optional;

/**
 * Puerto de salida: persistencia y consulta de ingredientes en la ontología.
 */
public interface IngredienteRepositoryPort {

    Optional<Ingrediente> buscarPorInci(Inci inci);

    /**
     * Busca un ingrediente por su nombre INCI normalizado; si no existe, lo persiste.
     */
    Ingrediente guardarOBuscar(Ingrediente ingrediente);
}
