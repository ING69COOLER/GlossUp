package com.glossup.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Objeto de valor monetario compartido por los módulos. Usa BigDecimal con 2 decimales para
 * evitar errores de redondeo. Regla: nunca negativo.
 */
public record Dinero(BigDecimal monto) {

    public Dinero {
        Objects.requireNonNull(monto, "monto requerido");
        if (monto.signum() < 0) {
            throw new ReglaNegocioException("El monto no puede ser negativo");
        }
        monto = monto.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero de(double valor) {
        return new Dinero(BigDecimal.valueOf(valor));
    }

    public static Dinero cero() {
        return new Dinero(BigDecimal.ZERO);
    }

    public Dinero mas(Dinero otro) {
        return new Dinero(this.monto.add(otro.monto));
    }

    public Dinero por(int cantidad) {
        if (cantidad < 0) {
            throw new ReglaNegocioException("La cantidad no puede ser negativa");
        }
        return new Dinero(this.monto.multiply(BigDecimal.valueOf(cantidad)));
    }
}
