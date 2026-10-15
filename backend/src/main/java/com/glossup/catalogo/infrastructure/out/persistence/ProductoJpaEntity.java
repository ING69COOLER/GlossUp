package com.glossup.catalogo.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad JPA de la tabla {@code producto}.
 *
 * <p>shortcut: no mapea {@code producto_ingrediente} (la ficha INCI). Editar los datos comerciales
 * (HU4) no la toca, y al no mapearla el UPDATE no puede borrarla. Añadir la colección cuando se
 * implemente la HU de gestión de ficha de ingredientes.</p>
 */
@Entity
@Table(name = "producto")
public class ProductoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "marca")
    private String marca;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "ph", precision = 3, scale = 1)
    private BigDecimal ph;

    @Column(name = "registro_invima")
    private String registroInvima;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    protected ProductoJpaEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendedorId() { return vendedorId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public BigDecimal getPh() { return ph; }
    public void setPh(BigDecimal ph) { this.ph = ph; }

    public String getRegistroInvima() { return registroInvima; }
    public void setRegistroInvima(String registroInvima) { this.registroInvima = registroInvima; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
