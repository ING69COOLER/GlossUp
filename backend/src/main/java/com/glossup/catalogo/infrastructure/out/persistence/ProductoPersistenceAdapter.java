package com.glossup.catalogo.infrastructure.out.persistence;

import com.glossup.catalogo.domain.model.ComposicionProducto;
import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Adaptador de salida: implementa {@link ProductoRepository} con Spring Data JPA. */
@Component
public class ProductoPersistenceAdapter implements ProductoRepository {

    private final SpringDataProductoRepository jpa;
    private final SpringDataIngredienteRepository ingredientesJpa;

    public ProductoPersistenceAdapter(SpringDataProductoRepository jpa,
                                      SpringDataIngredienteRepository ingredientesJpa) {
        this.jpa = jpa;
        this.ingredientesJpa = ingredientesJpa;
    }

    @Override
    public Producto guardar(Producto p) {
        ProductoJpaEntity e = p.getId() == null
                ? new ProductoJpaEntity()
                : jpa.findById(p.getId()).orElseGet(ProductoJpaEntity::new);
        e.setId(p.getId());
        e.setVendedorId(p.getVendedorId());
        e.setNombre(p.getNombre());
        e.setMarca(p.getMarca());
        e.setCategoria(p.getCategoria());
        e.setPrecio(p.getPrecio().monto());
        e.setStock(p.getStock());
        e.setPh(p.getPh() == null ? null : BigDecimal.valueOf(p.getPh()));
        e.setRegistroInvima(p.getRegistroInvima());
        e.setImagenUrl(p.getImagenUrl());
        e.setActivo(p.isActivo());
        e.setCreadoEn(p.getCreadoEn());
        sincronizarFicha(e, p.getComposicion());
        return aDominio(jpa.save(e));
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public List<Producto> buscarPorVendedor(Long vendedorId) {
        return jpa.findByVendedorIdOrderByCreadoEnDesc(vendedorId).stream().map(this::aDominio).toList();
    }

    /** Reemplaza las filas de producto_ingrediente con la ficha actual del dominio. */
    private static void sincronizarFicha(ProductoJpaEntity e, List<ComposicionProducto> composicion) {
        Set<ProductoIngredienteEmbeddable> filas = new LinkedHashSet<>();
        for (ComposicionProducto c : composicion) {
            Long ingId = Objects.requireNonNull(c.ingrediente().getId(),
                    "el ingrediente debe estar persistido (con id) antes de guardar la ficha");
            filas.add(new ProductoIngredienteEmbeddable(
                    ingId, c.concentracion() == null ? null : BigDecimal.valueOf(c.concentracion())));
        }
        e.getIngredientes().clear();
        e.getIngredientes().addAll(filas);
    }

    private Producto aDominio(ProductoJpaEntity e) {
        return new Producto(
                e.getId(), e.getVendedorId(), e.getNombre(), e.getMarca(), e.getCategoria(),
                new Dinero(e.getPrecio()), e.getStock(),
                e.getPh() == null ? null : e.getPh().doubleValue(),
                e.getRegistroInvima(), e.getImagenUrl(), e.isActivo(),
                cargarFicha(e.getIngredientes()), e.getCreadoEn());
    }

    /** Resuelve los ingredientes de la ficha (id → INCI) en una sola consulta. */
    private List<ComposicionProducto> cargarFicha(Set<ProductoIngredienteEmbeddable> filas) {
        if (filas.isEmpty()) {
            return List.of();
        }
        List<Long> ids = filas.stream().map(ProductoIngredienteEmbeddable::getIngredienteId).toList();
        Map<Long, IngredienteJpaEntity> porId = ingredientesJpa.findAllById(ids).stream()
                .collect(Collectors.toMap(IngredienteJpaEntity::getId, Function.identity()));
        return filas.stream()
                .map(f -> porId.get(f.getIngredienteId()))
                .filter(Objects::nonNull)
                .map(ing -> new ComposicionProducto(
                        new Ingrediente(ing.getId(), new Inci(ing.getNombreInci()), ing.getDescripcion()),
                        concentracionDe(filas, ing.getId())))
                .toList();
    }

    private static Double concentracionDe(Set<ProductoIngredienteEmbeddable> filas, Long ingredienteId) {
        return filas.stream()
                .filter(f -> ingredienteId.equals(f.getIngredienteId()))
                .map(ProductoIngredienteEmbeddable::getConcentracion)
                .findFirst()
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .orElse(null);
    }
}
