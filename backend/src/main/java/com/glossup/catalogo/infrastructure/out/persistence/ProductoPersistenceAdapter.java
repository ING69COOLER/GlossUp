package com.glossup.catalogo.infrastructure.out.persistence;

import com.glossup.catalogo.domain.model.Producto;
import com.glossup.catalogo.domain.port.out.ProductoRepository;
import com.glossup.shared.domain.Dinero;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa {@link ProductoRepository} con Spring Data JPA. */
@Component
public class ProductoPersistenceAdapter implements ProductoRepository {

    private final SpringDataProductoRepository jpa;

    public ProductoPersistenceAdapter(SpringDataProductoRepository jpa) {
        this.jpa = jpa;
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
        e.setActivo(p.isActivo());
        e.setCreadoEn(p.getCreadoEn());
        return aDominio(jpa.save(e));
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpa.findById(id).map(ProductoPersistenceAdapter::aDominio);
    }

    /** La ficha INCI (composición) no se carga: la HU4 no la usa (ver shortcut en la entidad). */
    private static Producto aDominio(ProductoJpaEntity e) {
        return new Producto(
                e.getId(), e.getVendedorId(), e.getNombre(), e.getMarca(), e.getCategoria(),
                new Dinero(e.getPrecio()), e.getStock(),
                e.getPh() == null ? null : e.getPh().doubleValue(),
                e.getRegistroInvima(), e.isActivo(), List.of(), e.getCreadoEn());
    }
}
