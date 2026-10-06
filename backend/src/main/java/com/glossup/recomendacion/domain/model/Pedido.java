package com.glossup.recomendacion.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Raíz de agregado: pedido confirmado (proceso "Gestión de ventas y checkout").
 *
 * <p>Reglas de negocio:</p>
 * <ul>
 *   <li>Un pedido nace en estado {@link EstadoPedido#CREADO} con al menos una línea.</li>
 *   <li>El total se calcula a partir de sus líneas (no se acepta de fuera).</li>
 *   <li>Los cambios de estado respetan las transiciones válidas de {@link EstadoPedido}.</li>
 * </ul>
 */
public class Pedido {

    private final Long id;
    private final Long usuarioId;
    private EstadoPedido estado;
    private final List<PedidoItem> items;
    private final Dinero total;
    private final OffsetDateTime creadoEn;

    public Pedido(Long id, Long usuarioId, EstadoPedido estado, List<PedidoItem> items,
                  OffsetDateTime creadoEn) {
        this.id = id;
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId requerido");
        if (items == null || items.isEmpty()) {
            throw new ReglaNegocioException("Un pedido debe tener al menos un producto");
        }
        this.items = List.copyOf(items);
        this.estado = estado != null ? estado : EstadoPedido.CREADO;
        this.total = items.stream().map(PedidoItem::subtotal).reduce(Dinero.cero(), Dinero::mas);
        this.creadoEn = creadoEn != null ? creadoEn : OffsetDateTime.now();
    }

    /** Crea un pedido nuevo (CREADO) a partir de las líneas de un carrito validado. */
    public static Pedido crear(Long usuarioId, List<PedidoItem> items) {
        return new Pedido(null, usuarioId, EstadoPedido.CREADO, items, OffsetDateTime.now());
    }

    // ---- Transiciones de estado ----

    public void marcarPagado() { transicionarA(EstadoPedido.PAGADO); }

    public void marcarEnviado() { transicionarA(EstadoPedido.ENVIADO); }

    public void cancelar() { transicionarA(EstadoPedido.CANCELADO); }

    private void transicionarA(EstadoPedido destino) {
        if (!estado.puedeTransicionarA(destino)) {
            throw new ReglaNegocioException(
                    "Transición de estado inválida: " + estado + " -> " + destino);
        }
        this.estado = destino;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public EstadoPedido getEstado() { return estado; }
    public List<PedidoItem> getItems() { return items; }
    public Dinero getTotal() { return total; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
