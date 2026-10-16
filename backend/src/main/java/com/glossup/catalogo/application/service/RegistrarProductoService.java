package com.glossup.catalogo.application.service;

import com.glossup.catalogo.domain.model.ComposicionProducto;
import com.glossup.catalogo.domain.model.ImagenProducto;
import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.in.RegistrarProductoCommand;
import com.glossup.catalogo.domain.port.in.RegistrarProductoUseCase;
import com.glossup.catalogo.domain.port.out.IngredienteRepositoryPort;
import com.glossup.catalogo.domain.port.out.ProductoRepositoryPort;
import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicación que implementa el caso de uso {@link RegistrarProductoUseCase}.
 * Orquesta la ontología de ingredientes, el agregado Producto y su persistencia.
 */
@Service
@Transactional
public class RegistrarProductoService implements RegistrarProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final IngredienteRepositoryPort ingredienteRepository;

    public RegistrarProductoService(ProductoRepositoryPort productoRepository,
                                    IngredienteRepositoryPort ingredienteRepository) {
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    public Producto registrar(RegistrarProductoCommand command) {
        if (command.ingredientes() == null || command.ingredientes().isEmpty()) {
            throw new ReglaNegocioException("La ficha técnica debe contener al menos un ingrediente (INCI)");
        }

        // 1. Resolver y asociar ingredientes en la ontología
        List<ComposicionProducto> composicion = new ArrayList<>();
        for (RegistrarProductoCommand.IngredienteComando ic : command.ingredientes()) {
            Inci inci = new Inci(ic.inci());
            Ingrediente ingrediente = ingredienteRepository.guardarOBuscar(
                    Ingrediente.nuevo(inci, ic.descripcion()));
            composicion.add(new ComposicionProducto(ingrediente, ic.concentracion()));
        }

        // 2. Mapear hipervínculos de imágenes
        List<ImagenProducto> imagenes = new ArrayList<>();
        if (command.urlsImagenes() != null) {
            for (String url : command.urlsImagenes()) {
                imagenes.add(new ImagenProducto(url));
            }
        }

        // 3. Crear el agregado de dominio (aplica todas las reglas e invariantes de negocio)
        Producto producto = Producto.registrar(
                command.vendedorId(),
                command.nombre(),
                command.marca(),
                command.categoria(),
                new Dinero(command.precio()),
                command.stock(),
                command.ph(),
                command.registroInvima(),
                imagenes,
                composicion
        );

        // 4. Persistir a través del puerto de salida
        return productoRepository.guardar(producto);
    }
}
