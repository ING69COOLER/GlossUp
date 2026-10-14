package com.glossup.perfil.infrastructure.out.persistence;

import com.glossup.perfil.domain.model.Ph;
import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.model.TipoPiel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración del adaptador de persistencia contra un PostgreSQL real (Testcontainers),
 * con el esquema aplicado por Flyway (V1) y validación de esquema de Hibernate activa
 * (ddl-auto=validate). Verifica que el mapeo JPA y las tablas de colección funcionan de verdad.
 *
 * <p>Requiere Docker en ejecución. Si no está disponible, la prueba se omite (no rompe el build).</p>
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PerfilPersistenceAdapter.class)
class PerfilPersistenceAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registro) {
        registro.add("spring.datasource.url", postgres::getJdbcUrl);
        registro.add("spring.datasource.username", postgres::getUsername);
        registro.add("spring.datasource.password", postgres::getPassword);
        registro.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registro.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    PerfilPersistenceAdapter adapter;

    @Autowired
    DataSource dataSource;

    private JdbcTemplate jdbc() {
        return new JdbcTemplate(dataSource);
    }

    /** Crea un usuario para satisfacer la FK perfil_dermatologico.usuario_id → usuario.id. */
    private Long insertarUsuario() {
        return jdbc().queryForObject(
                "INSERT INTO usuario(email, nombre, password_hash, activo) " +
                        "VALUES (?, ?, ?, true) RETURNING id",
                Long.class,
                "u" + System.nanoTime() + "@glossup.test", "Tester", "hash");
    }

    @Test
    void guarda_y_recupera_un_perfil_con_afecciones_y_alergias() {
        Long usuarioId = insertarUsuario();
        PerfilDermatologico nuevo = PerfilDermatologico.crear(usuarioId, new Ph(5.2), TipoPiel.MIXTA, "liso");
        nuevo.agregarAfeccion("acné");
        nuevo.agregarAlergia("parfum");

        PerfilDermatologico guardado = adapter.guardar(nuevo);
        assertNotNull(guardado.getId());

        Optional<PerfilDermatologico> recuperado = adapter.buscarPorUsuarioId(usuarioId);
        assertTrue(recuperado.isPresent());
        assertEquals(TipoPiel.MIXTA, recuperado.get().getTipoPiel());
        assertEquals(5.2, recuperado.get().getPh().valor(), 0.001);
        assertEquals(Set.of("acné"), recuperado.get().getAfecciones());
        assertEquals(Set.of("parfum"), recuperado.get().getAlergias());
        assertTrue(adapter.existePorUsuarioId(usuarioId));
    }

    @Test
    void existePorUsuarioId_es_falso_si_no_hay_perfil() {
        assertFalse(adapter.existePorUsuarioId(999_999L));
    }

    @Test
    void actualiza_las_colecciones_de_un_perfil_existente_HU2() {
        Long usuarioId = insertarUsuario();
        PerfilDermatologico perfil = PerfilDermatologico.crear(usuarioId, new Ph(5.0), TipoPiel.SECA, "liso");
        perfil.agregarAlergia("parfum");
        PerfilDermatologico guardado = adapter.guardar(perfil);

        // HU2: muta y vuelve a guardar (merge + reemplazo de la tabla de colección).
        guardado.quitarAlergia("parfum");
        guardado.agregarAlergia("alcohol denat");
        guardado.agregarAfeccion("rosácea");
        adapter.guardar(guardado);

        PerfilDermatologico recargado = adapter.buscarPorUsuarioId(usuarioId).orElseThrow();
        assertEquals(Set.of("alcohol denat"), recargado.getAlergias());
        assertEquals(Set.of("rosácea"), recargado.getAfecciones());
    }
}
