package com.glossup.catalogo.infrastructure.out.persistence;

import com.glossup.catalogo.domain.model.Inci;
import com.glossup.catalogo.domain.model.Ingrediente;
import com.glossup.catalogo.domain.port.out.IngredienteRepository;
import org.springframework.stereotype.Component;

/** Adaptador de salida: ontología de ingredientes con Spring Data JPA. */
@Component
public class IngredientePersistenceAdapter implements IngredienteRepository {

    private final SpringDataIngredienteRepository jpa;

    public IngredientePersistenceAdapter(SpringDataIngredienteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Ingrediente obtenerOCrear(Inci inci, String funcion) {
        // el INCI ya viene normalizado (minúsculas) por el value object → clave única estable
        String descripcion = (funcion == null || funcion.isBlank()) ? null : funcion.trim();
        IngredienteJpaEntity e = jpa.findByNombreInci(inci.nombre())
                .orElseGet(() -> jpa.save(new IngredienteJpaEntity(inci.nombre(), descripcion)));
        return new Ingrediente(e.getId(), new Inci(e.getNombreInci()), e.getDescripcion());
    }
}
