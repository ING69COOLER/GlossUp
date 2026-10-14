package com.glossup.shared.domain;

/**
 * Raíz de la jerarquía de excepciones de negocio del dominio. Se lanza cuando se viola una regla
 * de negocio o una invariante de un objeto del dominio.
 *
 * <p>Es abstracta a propósito: el código nunca lanza "una violación genérica", sino una de sus
 * subclases concretas, que expresan <em>la naturaleza</em> de la violación:</p>
 * <ul>
 *   <li>{@link DatoInvalidoException} — un valor rompe una invariante de formato o de rango.</li>
 *   <li>{@link OperacionNoPermitidaException} — la operación no es válida en el estado actual.</li>
 *   <li>{@link TransicionInvalidaException} — cambio de estado no permitido por la máquina de estados.</li>
 *   <li>{@link StockInsuficienteException} — conflicto por falta de inventario.</li>
 * </ul>
 *
 * <p>Toda subclase lleva un {@link CodigoError} estable. La capa web (adaptador de entrada) es la
 * única responsable de traducir estas excepciones a respuestas HTTP; el dominio no conoce HTTP.</p>
 */
public abstract class ReglaNegocioException extends RuntimeException {

    private final transient CodigoError codigo;

    protected ReglaNegocioException(CodigoError codigo, String mensaje) {
        super(mensaje != null ? mensaje : codigo.descripcion());
        this.codigo = codigo;
    }

    /** Código de error estable e independiente del idioma asociado a la violación. */
    public CodigoError getCodigo() {
        return codigo;
    }
}
