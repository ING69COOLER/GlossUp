package com.glossup.perfil.application.service;

import com.glossup.perfil.domain.model.Ph;
import com.glossup.perfil.domain.model.PerfilDermatologico;
import com.glossup.perfil.domain.port.in.ActualizarBiotipoComando;
import com.glossup.perfil.domain.port.in.CrearPerfilComando;
import com.glossup.perfil.domain.port.in.GestionarPerfilDermatologicoUseCase;
import com.glossup.perfil.domain.port.out.PerfilDermatologicoRepository;
import com.glossup.shared.domain.CodigoError;
import com.glossup.shared.domain.OperacionNoPermitidaException;
import com.glossup.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

/**
 * Servicio de aplicación del módulo de perfil: implementa el caso de uso de la HU1 orquestando el
 * dominio y el puerto de persistencia.
 *
 * <p>Responsabilidades de esta capa (no de la web ni del dominio):</p>
 * <ul>
 *   <li>Coordinar la transacción (@Transactional).</li>
 *   <li>Hacer cumplir reglas que requieren consultar el repositorio (un perfil por usuario).</li>
 *   <li>Traducir el comando de entrada a objetos del dominio (donde viven las invariantes).</li>
 * </ul>
 * No conoce HTTP ni JPA: solo depende de puertos e del modelo de dominio.
 */
@Service
public class PerfilDermatologicoService implements GestionarPerfilDermatologicoUseCase {

    private final PerfilDermatologicoRepository repositorio;

    public PerfilDermatologicoService(PerfilDermatologicoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public PerfilDermatologico crear(CrearPerfilComando comando) {
        Objects.requireNonNull(comando, "comando requerido");
        Long usuarioId = comando.usuarioId();

        // Regla de aplicación: un usuario tiene a lo sumo un perfil (columna usuario_id única).
        if (repositorio.existePorUsuarioId(usuarioId)) {
            throw new OperacionNoPermitidaException(CodigoError.PERFIL_YA_EXISTE,
                    "El usuario " + usuarioId + " ya tiene un perfil dermatológico");
        }

        // El dominio valida pH, normaliza y evita duplicados en afecciones/alergias.
        Ph ph = comando.ph() == null ? null : new Ph(comando.ph());
        PerfilDermatologico perfil =
                PerfilDermatologico.crear(usuarioId, ph, comando.tipoPiel(), comando.tipoCabello());
        aplicar(comando.afecciones(), perfil::agregarAfeccion);
        aplicar(comando.alergias(), perfil::agregarAlergia);

        return repositorio.guardar(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilDermatologico consultarPorUsuario(Long usuarioId) {
        return cargar(usuarioId);
    }

    // ---- HU2: gestión dinámica ----

    @Override
    @Transactional
    public PerfilDermatologico agregarAlergia(Long usuarioId, String alergia) {
        PerfilDermatologico perfil = cargar(usuarioId);
        perfil.agregarAlergia(alergia);
        return repositorio.guardar(perfil);
    }

    @Override
    @Transactional
    public PerfilDermatologico quitarAlergia(Long usuarioId, String alergia) {
        PerfilDermatologico perfil = cargar(usuarioId);
        perfil.quitarAlergia(alergia);
        return repositorio.guardar(perfil);
    }

    @Override
    @Transactional
    public PerfilDermatologico agregarAfeccion(Long usuarioId, String afeccion) {
        PerfilDermatologico perfil = cargar(usuarioId);
        perfil.agregarAfeccion(afeccion);
        return repositorio.guardar(perfil);
    }

    @Override
    @Transactional
    public PerfilDermatologico quitarAfeccion(Long usuarioId, String afeccion) {
        PerfilDermatologico perfil = cargar(usuarioId);
        perfil.quitarAfeccion(afeccion);
        return repositorio.guardar(perfil);
    }

    @Override
    @Transactional
    public PerfilDermatologico actualizarBiotipo(ActualizarBiotipoComando comando) {
        Objects.requireNonNull(comando, "comando requerido");
        PerfilDermatologico perfil = cargar(comando.usuarioId());
        Ph ph = comando.ph() == null ? null : new Ph(comando.ph());
        perfil.actualizarBiotipo(ph, comando.tipoPiel(), comando.tipoCabello());
        return repositorio.guardar(perfil);
    }

    /** Carga el perfil del usuario o falla con 404 si no existe. */
    private PerfilDermatologico cargar(Long usuarioId) {
        return repositorio.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException(
                        "El usuario " + usuarioId + " no tiene perfil dermatológico"));
    }

    private static void aplicar(Set<String> valores, java.util.function.Consumer<String> accion) {
        if (valores != null) {
            valores.forEach(accion);
        }
    }
}
