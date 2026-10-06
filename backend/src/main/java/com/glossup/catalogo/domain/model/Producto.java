package com.glossup.catalogo.domain.model;

import com.glossup.shared.domain.Dinero;
import com.glossup.shared.domain.ReglaNegocioException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio (raíz de agregado): producto cosmético del catálogo.
 *
 * <p>Reglas de negocio:</p>
 * <ul>
 *   <li>Nombre obligatorio; precio y stock nunca negativos.</li>
 *   <li>RNF-04: para publicarse (activarse) el producto debe tener ficha INCI (≥ 1 ingrediente).</li>
 *   <li>El stock se reduce al vender y no puede quedar negativo (HU9 reserva de inventario).</li>
 * </ul>
 */
public class Producto {

    private final Long id;
    private final Long vendedorId;
    private String nombre;
    private String marca;
    private String categoria;
    private Dinero precio;
    private int stock;
    private Double ph;
    private String registroInvima;
    private boolean activo;
    private final List<ComposicionProducto> composicion;
    private final OffsetDateTime creadoEn;

    public Producto(Long id, Long vendedorId, String nombre, String marca, String categoria,
                    Dinero precio, int stock, Double ph, String registroInvima, boolean activo,
                    List<ComposicionProducto> composicion, OffsetDateTime creadoEn) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.nombre = validarNombre(nombre);
        this.marca = normalizarOpcional(marca);
        this.categoria = normalizarOpcional(categoria);
        this.precio = Objects.requireNonNull(precio, "precio requerido");
        this.stock = validarStock(stock);
        this.ph = ph;
        this.registroInvima = normalizarOpcional(registroInvima);
        this.composicion = new ArrayList<>(composicion == null ? List.of() : composicion);
        this.activo = activo;
        this.creadoEn = creadoEn != null ? creadoEn : OffsetDateTime.now();
    }

    /** Fábrica para registrar un producto nuevo (HU3): inicia inactivo hasta tener ficha INCI. */
    public static Producto registrar(Long vendedorId, String nombre, String marca, String categoria,
                                     Dinero precio, int stock, Double ph, String registroInvima) {
        return new Producto(null, vendedorId, nombre, marca, categoria, precio, stock, ph,
                registroInvima, false, new ArrayList<>(), OffsetDateTime.now());
    }

    // ---- Ficha INCI ----

    public void agregarIngrediente(ComposicionProducto item) {
        Objects.requireNonNull(item, "composición requerida");
        boolean yaEsta = composicion.stream()
                .anyMatch(c -> c.ingrediente().getInci().equals(item.ingrediente().getInci()));
        if (yaEsta) {
            throw new ReglaNegocioException(
                    "El ingrediente ya está en la ficha: " + item.ingrediente().getInci());
        }
        composicion.add(item);
    }

    public boolean contieneIngrediente(Inci inci) {
        return composicion.stream().anyMatch(c -> c.ingrediente().getInci().equals(inci));
    }

    // ---- Publicación (RNF-04) ----

    /** Publica el producto. Regla: no se puede publicar sin ficha INCI. */
    public void publicar() {
        if (composicion.isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede publicar un producto sin ficha de ingredientes (INCI)");
        }
        this.activo = true;
    }

    public void despublicar() {
        this.activo = false;
    }

    // ---- Inventario (HU9) ----

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a reducir debe ser positiva");
        }
        if (cantidad > stock) {
            throw new ReglaNegocioException(
                    "Stock insuficiente para '" + nombre + "' (disponible: " + stock + ")");
        }
        this.stock -= cantidad;
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a reponer debe ser positiva");
        }
        this.stock += cantidad;
    }

    public boolean hayStock(int cantidad) {
        return stock >= cantidad;
    }

    // ---- Edición (HU4) ----

    public void actualizarDatos(String nombre, String marca, String categoria,
                                Dinero precio, Double ph, String registroInvima) {
        this.nombre = validarNombre(nombre);
        this.marca = normalizarOpcional(marca);
        this.categoria = normalizarOpcional(categoria);
        this.precio = Objects.requireNonNull(precio, "precio requerido");
        this.ph = ph;
        this.registroInvima = normalizarOpcional(registroInvima);
    }

    // ---- Validaciones ----

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio");
        }
        return nombre.trim();
    }

    private static int validarStock(int stock) {
        if (stock < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
        return stock;
    }

    private static String normalizarOpcional(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    // ---- Accesores ----

    public Long getId() { return id; }
    public Long getVendedorId() { return vendedorId; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public String getCategoria() { return categoria; }
    public Dinero getPrecio() { return precio; }
    public int getStock() { return stock; }
    public Double getPh() { return ph; }
    public String getRegistroInvima() { return registroInvima; }
    public boolean isActivo() { return activo; }
    public List<ComposicionProducto> getComposicion() { return List.copyOf(composicion); }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
