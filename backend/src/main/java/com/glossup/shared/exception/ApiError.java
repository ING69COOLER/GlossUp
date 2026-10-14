package com.glossup.shared.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Cuerpo de error estándar devuelto por la API (formato uniforme para todos los módulos).
 *
 * @param timestamp momento en que se produjo el error
 * @param status    código HTTP
 * @param error     frase estándar del estado HTTP (p. ej. "Unprocessable Entity")
 * @param codigo    código de error de negocio estable (ver {@code CodigoError}); {@code null} si
 *                  el error no proviene del dominio
 * @param message   mensaje legible para el cliente
 * @param path      ruta de la petición que falló
 * @param detalles  lista de detalles adicionales (p. ej. errores de validación por campo)
 */
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String codigo,
        String message,
        String path,
        List<String> detalles) {
}
