package com.glossup.shared.domain;

/**
 * Se lanza cuando se intenta un cambio de estado no permitido por la máquina de estados de un
 * agregado (p. ej. enviar un pedido que aún no está pagado).
 *
 * <p>Es un conflicto con el estado actual del recurso: la capa web la traduce a HTTP 409 (Conflict).</p>
 */
public class TransicionInvalidaException extends ReglaNegocioException {

    public TransicionInvalidaException(String mensaje) {
        super(CodigoError.TRANSICION_INVALIDA, mensaje);
    }
}
