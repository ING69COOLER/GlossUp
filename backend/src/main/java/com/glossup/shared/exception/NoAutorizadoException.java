package com.glossup.shared.exception;

/**
 * Se lanza cuando una petición no está autenticada correctamente: credenciales inválidas en el
 * login o una cuenta inactiva. Se traduce a HTTP 401 (Unauthorized).
 *
 * <p>Por seguridad, el mensaje es genérico ("credenciales inválidas") para no revelar si el fallo
 * fue por el correo o por la contraseña.</p>
 */
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
