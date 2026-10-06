package com.glossup.recomendacion.domain.model;

import java.util.Set;

/**
 * Estados del ciclo de vida de un pedido (proceso "Gestión de ventas y checkout").
 *
 * <p>Transiciones válidas:</p>
 * <pre>
 *   CREADO  -> PAGADO | CANCELADO
 *   PAGADO  -> ENVIADO | CANCELADO
 *   ENVIADO -> (final)
 *   CANCELADO -> (final)
 * </pre>
 */
public enum EstadoPedido {
    CREADO,
    PAGADO,
    ENVIADO,
    CANCELADO;

    private Set<EstadoPedido> siguientesPermitidos() {
        return switch (this) {
            case CREADO -> Set.of(PAGADO, CANCELADO);
            case PAGADO -> Set.of(ENVIADO, CANCELADO);
            case ENVIADO, CANCELADO -> Set.of();
        };
    }

    public boolean puedeTransicionarA(EstadoPedido destino) {
        return siguientesPermitidos().contains(destino);
    }
}
