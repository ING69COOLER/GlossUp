package com.glossup.shared.domain;

/**
 * Catálogo centralizado de códigos de error de negocio del dominio GlossUP.
 *
 * <p>Cada violación de una regla de negocio se identifica con un código estable e independiente
 * del idioma. El mensaje mostrado al usuario puede cambiar (o traducirse), pero el código no, de
 * modo que el frontend y las pruebas puedan reaccionar de forma determinista (p. ej. mostrar una
 * pantalla distinta ante {@code STOCK_INSUFICIENTE} que ante {@code PRODUCTO_SIN_INCI}).</p>
 *
 * <p>Es puro dominio: no conoce HTTP ni ningún framework. La traducción de código/excepción a un
 * estado HTTP vive en la capa web (adaptador de entrada).</p>
 */
public enum CodigoError {

    // ---- Validación de datos / invariantes de objetos de valor ----
    DATO_REQUERIDO("Un dato obligatorio llegó vacío o nulo"),
    EMAIL_INVALIDO("El correo electrónico no tiene un formato válido"),
    NOMBRE_INVALIDO("El nombre es obligatorio"),
    PASSWORD_HASH_REQUERIDO("El hash de la contraseña es obligatorio"),
    PH_FUERA_DE_RANGO("El pH debe estar en la escala 0–14"),
    MONTO_NEGATIVO("Un importe monetario no puede ser negativo"),
    CANTIDAD_INVALIDA("La cantidad debe ser un entero positivo"),
    CONCENTRACION_INVALIDA("La concentración debe estar entre 0 y 100 %"),
    SCORE_FUERA_DE_RANGO("El puntaje de recomendación debe estar entre 0.0 y 1.0"),
    STOCK_NEGATIVO("El stock no puede ser negativo"),
    ALERTA_INCOMPLETA("La alerta de compatibilidad está incompleta (RNF-05: debe explicar su causa)"),
    REGLA_INVALIDA("La regla de compatibilidad es inconsistente"),
    PEDIDO_SIN_ITEMS("Un pedido debe tener al menos una línea"),
    RECOMENDACION_SIN_RAZON("La recomendación debe incluir una razón"),

    // ---- Operaciones no permitidas por el estado del agregado ----
    USUARIO_SIN_ROLES("El usuario debe conservar al menos un rol"),
    PRODUCTO_SIN_INCI("No se puede publicar un producto sin ficha de ingredientes INCI (RNF-04)"),
    INGREDIENTE_DUPLICADO("El ingrediente ya está en la ficha del producto"),
    ITEM_NO_EN_CARRITO("El producto no está en el carrito"),
    CARRITO_VACIO("No se puede confirmar la compra de un carrito vacío"),

    // ---- Conflictos de estado ----
    STOCK_INSUFICIENTE("No hay stock suficiente para la cantidad solicitada"),
    TRANSICION_INVALIDA("La transición de estado del pedido no está permitida");

    private final String descripcion;

    CodigoError(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Descripción por defecto (sirve de mensaje cuando no se da uno más específico). */
    public String descripcion() {
        return descripcion;
    }
}
