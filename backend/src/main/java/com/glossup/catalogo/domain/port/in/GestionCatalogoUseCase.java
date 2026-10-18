package com.glossup.catalogo.domain.port.in;

import com.glossup.catalogo.domain.model.Producto;

import java.util.List;

/**
 * Puerto de entrada: gestión del catálogo por el vendedor (HU3 y afines). Toda operación es sobre
 * productos propios del vendedor autenticado; si el producto no existe o es de otro vendedor se
 * responde 404 (no se revela su existencia).
 */
public interface GestionCatalogoUseCase {

    Producto crear(CrearProductoComando comando);

    List<Producto> listarDelVendedor(Long vendedorId);

    Producto obtener(Long productoId, Long vendedorId);

    /** Fija la disponibilidad (stock absoluto) del producto. */
    Producto actualizarStock(Long productoId, Long vendedorId, int stock);

    /** Añade un ingrediente a la ficha INCI (lo crea en la ontología si no existe, con su función). */
    Producto agregarIngrediente(Long productoId, Long vendedorId, String nombreInci, Double concentracion, String funcion);

    Producto quitarIngrediente(Long productoId, Long vendedorId, String nombreInci);

    /** Publica el producto. Regla RNF-04: requiere al menos un ingrediente en la ficha INCI. */
    Producto publicar(Long productoId, Long vendedorId);

    Producto despublicar(Long productoId, Long vendedorId);
}
