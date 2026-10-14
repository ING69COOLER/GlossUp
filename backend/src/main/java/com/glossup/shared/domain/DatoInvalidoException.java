package com.glossup.shared.domain;

/**
 * Se lanza cuando un valor rompe una invariante de formato o de rango de un objeto del dominio
 * (correo mal formado, pH fuera de 0–14, importe negativo, cantidad no positiva, concentración
 * fuera de 0–100 %, campos obligatorios vacíos, etc.).
 *
 * <p>Representa datos de entrada que el dominio no puede aceptar. La capa web la traduce a
 * HTTP 422 (Unprocessable Entity).</p>
 */
public class DatoInvalidoException extends ReglaNegocioException {

    public DatoInvalidoException(CodigoError codigo, String mensaje) {
        super(codigo, mensaje);
    }

    /** Atajo para un dato obligatorio ausente. */
    public static DatoInvalidoException requerido(String campo) {
        return new DatoInvalidoException(CodigoError.DATO_REQUERIDO,
                "El campo '" + campo + "' es obligatorio");
    }
}
