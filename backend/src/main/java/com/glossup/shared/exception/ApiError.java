package com.glossup.shared.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Cuerpo de error estandar devuelto por la API (formato uniforme para todos los modulos).
 */
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> detalles) {
}
