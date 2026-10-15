package com.glossup.shared.exception;

import com.glossup.shared.domain.ReglaNegocioException;
import com.glossup.shared.domain.StockInsuficienteException;
import com.glossup.shared.domain.TransicionInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Manejo global de errores (@ControllerAdvice) para toda la aplicación. Centraliza la traducción
 * de excepciones a respuestas HTTP con el cuerpo {@link ApiError}, y es el <em>único</em> punto
 * donde el dominio (que no conoce HTTP) se mapea a códigos de estado.
 *
 * <p>Mapeo:</p>
 * <ul>
 *   <li>{@link NotFoundException} → 404 Not Found.</li>
 *   <li>{@link StockInsuficienteException}, {@link TransicionInvalidaException} → 409 Conflict
 *       (conflicto con el estado actual del recurso).</li>
 *   <li>Resto de {@link ReglaNegocioException} (datos inválidos, operación no permitida) → 422.</li>
 *   <li>{@link MethodArgumentNotValidException} (Bean Validation) → 400 Bad Request.</li>
 *   <li>Cualquier otra excepción → 500 Internal Server Error.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, null, ex.getMessage(), req, List.of());
    }

    /** Fallo de autenticación (credenciales inválidas, cuenta inactiva). */
    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<ApiError> handleNoAutorizado(NoAutorizadoException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "NO_AUTORIZADO", ex.getMessage(), req, List.of());
    }

    /** Conflictos con el estado del recurso (más específico que {@link ReglaNegocioException}). */
    @ExceptionHandler({StockInsuficienteException.class, TransicionInvalidaException.class})
    public ResponseEntity<ApiError> handleConflicto(ReglaNegocioException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getCodigo().name(), ex.getMessage(), req, List.of());
    }

    /** Resto de violaciones de reglas de negocio: datos inválidos u operaciones no permitidas. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> handleReglaNegocio(ReglaNegocioException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getCodigo().name(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDACION", "Error de validación", req, detalles);
    }

    /** Cuerpo JSON mal formado o valor no convertible (p. ej. un tipo de piel inexistente). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBodyIlegible(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO",
                "El cuerpo de la petición es inválido o está mal formado", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage(), req, List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String codigo, String message,
                                           HttpServletRequest req, List<String> detalles) {
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                codigo,
                message,
                req.getRequestURI(),
                detalles);
        return ResponseEntity.status(status).body(body);
    }
}
