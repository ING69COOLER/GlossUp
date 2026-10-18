package com.glossup.catalogo.infrastructure.in.rest;

import com.glossup.catalogo.domain.port.in.EditarProductoUseCase;
import com.glossup.catalogo.domain.port.in.GestionCatalogoUseCase;
import com.glossup.catalogo.infrastructure.in.rest.dto.ActualizarStockRequest;
import com.glossup.catalogo.infrastructure.in.rest.dto.AgregarIngredienteRequest;
import com.glossup.catalogo.infrastructure.in.rest.dto.CrearProductoRequest;
import com.glossup.catalogo.infrastructure.in.rest.dto.EditarProductoRequest;
import com.glossup.catalogo.infrastructure.in.rest.dto.ProductoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST del catálogo del vendedor (HU3-HU4). Toda la ruta exige el rol VENDEDOR
 * (RNF-14) y el {@code vendedorId} se toma del subject del JWT: un vendedor solo ve y toca sus
 * productos. Los valores sensibles (stock, INCI) viajan en el cuerpo, nunca en la URL.
 */
@RestController
@RequestMapping("/api/catalogo/productos")
@PreAuthorize("hasRole('VENDEDOR')")
@Tag(name = "Catálogo", description = "Gestión de productos del vendedor")
@SecurityRequirement(name = "bearer-jwt")
public class ProductoController {

    private final EditarProductoUseCase editarProducto;
    private final GestionCatalogoUseCase gestion;

    public ProductoController(EditarProductoUseCase editarProducto, GestionCatalogoUseCase gestion) {
        this.editarProducto = editarProducto;
        this.gestion = gestion;
    }

    @PostMapping
    @Operation(summary = "Registra un producto nuevo del vendedor (HU3); nace como borrador")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody CrearProductoRequest request,
                                                  Authentication auth) {
        ProductoResponse creado = ProductoResponse.desde(gestion.crear(request.aComando(vendedorId(auth))));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/mios")
    @Operation(summary = "Lista los productos del vendedor autenticado")
    public List<ProductoResponse> mios(Authentication auth) {
        return gestion.listarDelVendedor(vendedorId(auth)).stream().map(ProductoResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto propio con su ficha INCI")
    public ProductoResponse obtener(@PathVariable Long id, Authentication auth) {
        return ProductoResponse.desde(gestion.obtener(id, vendedorId(auth)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita los datos de un producto del vendedor (HU4)")
    public ProductoResponse editar(@PathVariable Long id,
                                   @Valid @RequestBody EditarProductoRequest request,
                                   Authentication auth) {
        return ProductoResponse.desde(editarProducto.editar(request.aComando(id, vendedorId(auth))));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Fija la disponibilidad (stock) de un producto")
    public ProductoResponse actualizarStock(@PathVariable Long id,
                                            @Valid @RequestBody ActualizarStockRequest request,
                                            Authentication auth) {
        return ProductoResponse.desde(gestion.actualizarStock(id, vendedorId(auth), request.stock()));
    }

    @PostMapping("/{id}/ingredientes")
    @Operation(summary = "Añade un ingrediente (INCI) a la ficha del producto")
    public ProductoResponse agregarIngrediente(@PathVariable Long id,
                                               @Valid @RequestBody AgregarIngredienteRequest request,
                                               Authentication auth) {
        return ProductoResponse.desde(
                gestion.agregarIngrediente(id, vendedorId(auth), request.nombreInci(), request.concentracion(), request.funcion()));
    }

    @DeleteMapping("/{id}/ingredientes")
    @Operation(summary = "Quita un ingrediente de la ficha (INCI en query para soportar espacios)")
    public ProductoResponse quitarIngrediente(@PathVariable Long id,
                                              @RequestParam String inci,
                                              Authentication auth) {
        return ProductoResponse.desde(gestion.quitarIngrediente(id, vendedorId(auth), inci));
    }

    @PostMapping("/{id}/publicar")
    @Operation(summary = "Publica el producto (RNF-04: requiere ficha INCI)")
    public ProductoResponse publicar(@PathVariable Long id, Authentication auth) {
        return ProductoResponse.desde(gestion.publicar(id, vendedorId(auth)));
    }

    @PostMapping("/{id}/despublicar")
    @Operation(summary = "Retira el producto de la vitrina")
    public ProductoResponse despublicar(@PathVariable Long id, Authentication auth) {
        return ProductoResponse.desde(gestion.despublicar(id, vendedorId(auth)));
    }

    private static Long vendedorId(Authentication auth) {
        return Long.valueOf(auth.getName());
    }
}
