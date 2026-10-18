package com.glossup.catalogo.infrastructure.out.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/** Entidad JPA de la tabla {@code producto} con su ficha INCI ({@code producto_ingrediente}). */
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

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_ingrediente", joinColumns = @JoinColumn(name = "producto_id"))
    private Set<ProductoIngredienteEmbeddable> ingredientes = new LinkedHashSet<>();

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

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public Set<ProductoIngredienteEmbeddable> getIngredientes() { return ingredientes; }
    public void setIngredientes(Set<ProductoIngredienteEmbeddable> ingredientes) { this.ingredientes = ingredientes; }
}
