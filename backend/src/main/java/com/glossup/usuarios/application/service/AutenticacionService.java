package com.glossup.usuarios.application.service;

import com.glossup.shared.domain.CodigoError;
import com.glossup.shared.domain.OperacionNoPermitidaException;
import com.glossup.shared.exception.NoAutorizadoException;
import com.glossup.shared.exception.NotFoundException;
import com.glossup.usuarios.domain.model.Email;
import com.glossup.usuarios.domain.model.Rol;
import com.glossup.usuarios.domain.model.Usuario;
import com.glossup.usuarios.domain.port.in.AutenticarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.CredencialesComando;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioComando;
import com.glossup.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import com.glossup.usuarios.domain.port.in.ResultadoAutenticacion;
import com.glossup.usuarios.domain.port.out.CifradorDePassword;
import com.glossup.usuarios.domain.port.out.GeneradorDeToken;
import com.glossup.usuarios.domain.port.out.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Servicio de aplicación del módulo de usuarios: implementa el registro y el login.
 *
 * <p>Solo depende de puertos (repositorio, cifrador, generador de token) y del modelo de dominio.
 * Responsabilidades: cifrar la contraseña antes de persistir, hacer cumplir la unicidad del correo,
 * verificar credenciales de forma segura y emitir el token con el id de usuario y sus roles.</p>
 */
@Service
public class AutenticacionService implements RegistrarUsuarioUseCase, AutenticarUsuarioUseCase {

    private static final String MENSAJE_CREDENCIALES = "Credenciales inválidas";

    private final UsuarioRepository repositorio;
    private final CifradorDePassword cifrador;
    private final GeneradorDeToken generadorDeToken;

    public AutenticacionService(UsuarioRepository repositorio, CifradorDePassword cifrador,
                                GeneradorDeToken generadorDeToken) {
        this.repositorio = repositorio;
        this.cifrador = cifrador;
        this.generadorDeToken = generadorDeToken;
    }

    @Override
    @Transactional
    public Usuario registrar(RegistrarUsuarioComando comando) {
        Objects.requireNonNull(comando, "comando requerido");
        Email email = new Email(comando.email()); // valida formato y normaliza

        if (repositorio.existePorEmail(email)) {
            throw new OperacionNoPermitidaException(CodigoError.EMAIL_YA_REGISTRADO,
                    "Ya existe un usuario con el correo " + email);
        }

        Rol rol = comando.rolInicial() == null ? Rol.CLIENTE : comando.rolInicial();
        // Seguridad: nadie se auto-registra como ADMIN; ese rol lo provisiona otro administrador.
        if (rol == Rol.ADMIN) {
            throw new OperacionNoPermitidaException(CodigoError.ROL_NO_AUTOREGISTRABLE,
                    "No es posible registrarse como administrador");
        }

        String hash = cifrador.cifrar(comando.password());
        Usuario usuario = Usuario.registrar(email, comando.nombre(), hash); // nace CLIENTE

        if (rol != Rol.CLIENTE) {
            usuario.asignarRol(rol);
            usuario.quitarRol(Rol.CLIENTE); // el usuario queda con un único rol (su panel)
        }
        return repositorio.guardar(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoAutenticacion autenticar(CredencialesComando comando) {
        Objects.requireNonNull(comando, "comando requerido");
        Email email = emailOrNull(comando.email());
        if (email == null) {
            throw new NoAutorizadoException(MENSAJE_CREDENCIALES);
        }

        Usuario usuario = repositorio.buscarPorEmail(email)
                .orElseThrow(() -> new NoAutorizadoException(MENSAJE_CREDENCIALES));

        if (!cifrador.coincide(comando.password(), usuario.getPasswordHash())) {
            throw new NoAutorizadoException(MENSAJE_CREDENCIALES);
        }
        if (!usuario.isActivo()) {
            throw new NoAutorizadoException("La cuenta está inactiva");
        }

        List<String> roles = usuario.getRoles().stream().map(Rol::name).toList();
        String token = generadorDeToken.generar(usuario.getId(), roles);
        return new ResultadoAutenticacion(token, usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario consultarPorId(Long usuarioId) {
        return repositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario " + usuarioId + " no encontrado"));
    }

    /** Un correo mal formado en el login es un fallo de autenticación, no un 422. */
    private static Email emailOrNull(String valor) {
        try {
            return new Email(valor);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
