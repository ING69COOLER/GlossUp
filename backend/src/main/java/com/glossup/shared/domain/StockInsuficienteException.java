package com.glossup.shared.domain;

/**
 * Se lanza al intentar reservar/vender más unidades de las disponibles en inventario
 * (proceso de ventas y checkout, HU9).
 *
 * <p>Lleva los datos estructurados del conflicto ({@code disponible}/{@code solicitado}) para que
 * la capa de aplicación o el frontend puedan reaccionar sin parsear el mensaje. Es un conflicto
 * con el estado del recurso: la capa web la traduce a HTTP 409 (Conflict).</p>
 */
public class StockInsuficienteException extends ReglaNegocioException {

    private final int disponible;
    private final int solicitado;

    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super(CodigoError.STOCK_INSUFICIENTE,
                "Stock insuficiente para '" + producto + "' (disponible: " + disponible
                        + ", solicitado: " + solicitado + ")");
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public int getDisponible() {
        return disponible;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
