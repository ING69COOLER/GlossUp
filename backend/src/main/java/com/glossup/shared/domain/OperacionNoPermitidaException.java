package com.glossup.shared.domain;

/**
 * Se lanza cuando una operación es sintácticamente correcta pero no está permitida por el estado
 * o las reglas del agregado (publicar un producto sin ficha INCI, dejar a un usuario sin roles,
 * repetir un ingrediente en la ficha, operar sobre un ítem que no está en el carrito, etc.).
 *
 * <p>La capa web la traduce a HTTP 422 (Unprocessable Entity).</p>
 */
public class OperacionNoPermitidaException extends ReglaNegocioException {

    public OperacionNoPermitidaException(CodigoError codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
