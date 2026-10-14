package com.glossup.perfil.application.service;

import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.model.TipoPiel;
import com.glossup.perfil.domain.port.in.ActualizarBiotipoComando;
import com.glossup.perfil.domain.port.in.CrearPerfilComando;
import com.glossup.perfil.domain.port.out.PerfilDermatologicoRepository;
import com.glossup.shared.domain.CodigoError;
import com.glossup.shared.domain.DatoInvalidoException;
import com.glossup.shared.domain.OperacionNoPermitidaException;
import com.glossup.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del caso de uso de la HU1. Usan un repositorio falso en memoria (doble de prueba del
 * puerto de salida): la lógica de aplicación se verifica sin base de datos ni Spring.
 */
class PerfilDermatologicoServiceTest {

    /** Doble de prueba del puerto {@link PerfilDermatologicoRepository}. */
    private static class RepositorioEnMemoria implements PerfilDermatologicoRepository {
        private final Map<Long, PerfilDermatologico> porUsuario = new HashMap<>();
        private final AtomicLong secuencia = new AtomicLong(0);

        @Override
        public PerfilDermatologico guardar(PerfilDermatologico perfil) {
            Long id = perfil.getId() != null ? perfil.getId() : secuencia.incrementAndGet();
            PerfilDermatologico persistido = new PerfilDermatologico(
                    id, perfil.getUsuarioId(), perfil.getPh(), perfil.getTipoPiel(),
                    perfil.getTipoCabello(), perfil.getAfecciones(), perfil.getAlergias(),
                    perfil.getActualizadoEn());
            porUsuario.put(persistido.getUsuarioId(), persistido);
            return persistido;
        }

        @Override
        public Optional<PerfilDermatologico> buscarPorUsuarioId(Long usuarioId) {
            return Optional.ofNullable(porUsuario.get(usuarioId));
        }

        @Override
        public boolean existePorUsuarioId(Long usuarioId) {
            return porUsuario.containsKey(usuarioId);
        }
    }

    private PerfilDermatologicoService service;

    @BeforeEach
    void setUp() {
        service = new PerfilDermatologicoService(new RepositorioEnMemoria());
    }

    @Test
    void crea_el_perfil_de_un_usuario_nuevo_normalizando_datos() {
        CrearPerfilComando cmd = new CrearPerfilComando(
                7L, 5.2, TipoPiel.MIXTA, "liso", Set.of("Acné"), Set.of("Parfum", "parfum"));

        PerfilDermatologico perfil = service.crear(cmd);

        assertNotNull(perfil.getId());
        assertEquals(7L, perfil.getUsuarioId());
        assertEquals(TipoPiel.MIXTA, perfil.getTipoPiel());
        assertEquals(1, perfil.getAlergias().size(), "las alergias se normalizan sin duplicados");
        assertTrue(perfil.esAlergicoA("PARFUM"));
    }

    @Test
    void no_permite_dos_perfiles_para_el_mismo_usuario() {
        service.crear(new CrearPerfilComando(7L, null, null, null, Set.of(), Set.of()));

        OperacionNoPermitidaException ex = assertThrows(OperacionNoPermitidaException.class,
                () -> service.crear(new CrearPerfilComando(7L, null, null, null, Set.of(), Set.of())));
        assertEquals(CodigoError.PERFIL_YA_EXISTE, ex.getCodigo());
    }

    @Test
    void rechaza_ph_fuera_de_rango_delegando_en_el_dominio() {
        CrearPerfilComando cmd = new CrearPerfilComando(7L, 15.0, null, null, Set.of(), Set.of());
        DatoInvalidoException ex = assertThrows(DatoInvalidoException.class, () -> service.crear(cmd));
        assertEquals(CodigoError.PH_FUERA_DE_RANGO, ex.getCodigo());
    }

    @Test
    void consultar_sin_perfil_lanza_NotFound() {
        assertThrows(NotFoundException.class, () -> service.consultarPorUsuario(99L));
    }

    @Test
    void consultar_devuelve_el_perfil_creado() {
        service.crear(new CrearPerfilComando(7L, 5.0, TipoPiel.SECA, "rizado", Set.of(), Set.of()));
        PerfilDermatologico encontrado = service.consultarPorUsuario(7L);
        assertEquals(TipoPiel.SECA, encontrado.getTipoPiel());
        assertTrue(encontrado.getActualizadoEn().isBefore(OffsetDateTime.now().plusSeconds(1)));
    }

    // ---- HU2 ----

    @Test
    void agrega_y_quita_alergias_sobre_el_perfil_existente() {
        service.crear(new CrearPerfilComando(7L, null, null, null, Set.of(), Set.of()));

        PerfilDermatologico conAlergia = service.agregarAlergia(7L, "Parfum");
        assertTrue(conAlergia.esAlergicoA("parfum"));

        PerfilDermatologico sinAlergia = service.quitarAlergia(7L, "parfum");
        assertFalse(sinAlergia.esAlergicoA("parfum"));
    }

    @Test
    void agrega_afeccion_y_actualiza_biotipo() {
        service.crear(new CrearPerfilComando(7L, null, null, null, Set.of(), Set.of()));

        assertTrue(service.agregarAfeccion(7L, "Rosácea").tieneAfeccion("rosácea"));

        PerfilDermatologico actualizado =
                service.actualizarBiotipo(new ActualizarBiotipoComando(7L, 6.0, TipoPiel.GRASA, "ondulado"));
        assertEquals(TipoPiel.GRASA, actualizado.getTipoPiel());
        assertEquals(6.0, actualizado.getPh().valor(), 0.001);
    }

    @Test
    void gestionar_perfil_inexistente_lanza_NotFound() {
        assertThrows(NotFoundException.class, () -> service.agregarAlergia(404L, "parfum"));
        assertThrows(NotFoundException.class,
                () -> service.actualizarBiotipo(new ActualizarBiotipoComando(404L, 5.0, null, null)));
    }
}
