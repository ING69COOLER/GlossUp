package com.glossup.catalogo.infrastructure.in.rest;

import com.glossup.catalogo.domain.port.in.EditarProductoUseCase;
import com.glossup.catalogo.infrastructure.in.rest.dto.EditarProductoRequest;
import com.glossup.catalogo.infrastructure.in.rest.dto.ProductoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST del catálogo (HU4). El vendedor autenticado edita sus productos; el
 * {@code vendedorId} se toma del subject del JWT y la ruta exige el rol VENDEDOR (RNF-14).
 */
@RestController
@RequestMapping("/api/catalogo/productos")
@Tag(name = "Catálogo", description = "Gestión de productos del vendedor")
@SecurityRequirement(name = "bearer-jwt")
public class ProductoController {

    private final EditarProductoUseCase editarProducto;

    public ProductoController(EditarProductoUseCase editarProducto) {
        this.editarProducto = editarProducto;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDEDOR')")
    @Operation(summary = "Edita los datos de un producto del vendedor (HU4)")
    public ProductoResponse editar(@PathVariable Long id,
                                   @Valid @RequestBody EditarProductoRequest request,
                                   Authentication authentication) {
        Long vendedorId = Long.valueOf(authentication.getName());
        return ProductoResponse.desde(editarProducto.editar(request.aComando(id, vendedorId)));
    }
}
