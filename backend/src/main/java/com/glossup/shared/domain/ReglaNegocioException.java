package com.glossup.shared.domain;

/**
 * Excepción del dominio: se lanza cuando se viola una regla de negocio o una invariante de un
 * objeto del dominio (p. ej. un precio negativo, un pH fuera de rango, una transición de estado
 * inválida). Es independiente de cualquier framework.
 *
 * <p>La capa web la traduce a una respuesta HTTP 422 (Unprocessable Entity).</p>
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
