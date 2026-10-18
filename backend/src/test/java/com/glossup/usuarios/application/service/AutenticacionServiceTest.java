package com.glossup.usuarios.application.service;

import com.glossup.shared.domain.CodigoError;
import com.glossup.shared.domain.OperacionNoPermitidaException;
import com.glossup.shared.exception.NoAutorizadoException;
import com.glossup.shared.exception.NotFoundException;
import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.in.CredencialesComando;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioComando;
import com.glossup.usuarios.domain.port.in.ResultadoAutenticacion;
import com.glossup.usuarios.domain.port.out.CifradorDePassword;
import com.glossup.usuarios.domain.port.out.GeneradorDeToken;
import com.glossup.usuarios.domain.port.out.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del registro y login con dobles en memoria de los puertos (sin BD, sin Spring).
 */
class AutenticacionServiceTest {

    /** Repositorio falso en memoria. */
    private static class RepoEnMemoria implements UsuarioRepository {
        private final Map<String, Usuario> porEmail = new HashMap<>();
        private final Map<Long, Usuario> porId = new HashMap<>();
        private final AtomicLong seq = new AtomicLong(0);

        @Override
        public Usuario guardar(Usuario u) {
            Long id = u.getId() != null ? u.getId() : seq.incrementAndGet();
            Usuario persistido = new Usuario(id, u.getEmail(), u.getNombre(), u.getPasswordHash(),
                    u.isActivo(), u.getRoles(), u.getCreadoEn());
            porEmail.put(u.getEmail().valor(), persistido);
            porId.put(id, persistido);
            return persistido;
        }

        @Override public Optional<Usuario> buscarPorEmail(Email e) { return Optional.ofNullable(porEmail.get(e.valor())); }
        @Override public Optional<Usuario> buscarPorId(Long id) { return Optional.ofNullable(porId.get(id)); }
        @Override public boolean existePorEmail(Email e) { return porEmail.containsKey(e.valor()); }
    }

    /** Cifrador falso determinista (NO usar en producción). */
    private static class CifradorFalso implements CifradorDePassword {
        @Override public String cifrar(String plano) { return "hash:" + plano; }
        @Override public boolean coincide(String plano, String hash) { return hash.equals("hash:" + plano); }
    }

    private RepoEnMemoria repo;
    private AutenticacionService service;

    @BeforeEach
    void setUp() {
        repo = new RepoEnMemoria();
        GeneradorDeToken token = (id, roles) -> "token-" + id + "-" + String.join(",", roles);
        service = new AutenticacionService(repo, new CifradorFalso(), token);
    }

    @Test
    void registra_cliente_por_defecto_y_cifra_la_password() {
        Usuario u = service.registrar(new RegistrarUsuarioComando("Ana@Mail.com", "Ana", "secret123", null));

        assertNotNull(u.getId());
        assertEquals("ana@mail.com", u.getEmail().valor(), "el email se normaliza");
        assertTrue(u.tieneRol(Rol.CLIENTE));
        assertEquals("hash:secret123", u.getPasswordHash(), "nunca se guarda la contraseña en claro");
    }

    @Test
    void registra_con_rol_vendedor_como_unico_rol() {
        Usuario u = service.registrar(new RegistrarUsuarioComando("v@mail.com", "Vendedor", "secret123", Rol.VENDEDOR));
        assertTrue(u.tieneRol(Rol.VENDEDOR));
        assertFalse(u.tieneRol(Rol.CLIENTE));
        assertEquals(1, u.getRoles().size());
    }

    @Test
    void no_permite_correo_duplicado() {
        service.registrar(new RegistrarUsuarioComando("a@mail.com", "Ana", "secret123", null));
        OperacionNoPermitidaException ex = assertThrows(OperacionNoPermitidaException.class,
                () -> service.registrar(new RegistrarUsuarioComando("A@Mail.com", "Otra", "secret123", null)));
        assertEquals(CodigoError.EMAIL_YA_REGISTRADO, ex.getCodigo());
    }

    @Test
    void login_correcto_devuelve_token_con_id_y_roles() {
        service.registrar(new RegistrarUsuarioComando("a@mail.com", "Ana", "secret123", Rol.VENDEDOR));

        ResultadoAutenticacion r = service.autenticar(new CredencialesComando("a@mail.com", "secret123"));

        assertTrue(r.token().startsWith("token-" + r.usuario().getId()));
        assertTrue(r.token().contains("VENDEDOR"));
    }

    @Test
    void no_permite_auto_registrarse_como_admin() {
        OperacionNoPermitidaException ex = assertThrows(OperacionNoPermitidaException.class,
                () -> service.registrar(new RegistrarUsuarioComando("hacker@mail.com", "Hacker", "secret123", Rol.ADMIN)));
        assertEquals(CodigoError.ROL_NO_AUTOREGISTRABLE, ex.getCodigo());
    }

    @Test
    void login_con_password_incorrecta_es_401() {
        service.registrar(new RegistrarUsuarioComando("a@mail.com", "Ana", "secret123", null));
        assertThrows(NoAutorizadoException.class,
                () -> service.autenticar(new CredencialesComando("a@mail.com", "otra")));
    }

    @Test
    void login_con_email_inexistente_es_401() {
        assertThrows(NoAutorizadoException.class,
                () -> service.autenticar(new CredencialesComando("nadie@mail.com", "secret123")));
    }

    @Test
    void login_con_cuenta_inactiva_es_401() {
        Usuario u = service.registrar(new RegistrarUsuarioComando("a@mail.com", "Ana", "secret123", null));
        // desactivar y re-guardar directamente en el repo
        Usuario inactivo = new Usuario(u.getId(), u.getEmail(), u.getNombre(), u.getPasswordHash(),
                false, u.getRoles(), OffsetDateTime.now());
        repo.guardar(inactivo);

        assertThrows(NoAutorizadoException.class,
                () -> service.autenticar(new CredencialesComando("a@mail.com", "secret123")));
    }

    @Test
    void consultar_por_id_inexistente_es_404() {
        assertThrows(NotFoundException.class, () -> service.consultarPorId(999L));
    }

    @Test
    void consultar_por_id_devuelve_el_usuario() {
        Usuario u = service.registrar(new RegistrarUsuarioComando("a@mail.com", "Ana", "secret123", null));
        assertEquals("Ana", service.consultarPorId(u.getId()).getNombre());
        assertEquals(List.of("CLIENTE"), service.consultarPorId(u.getId()).getRoles().stream().map(Rol::name).toList());
    }
}
