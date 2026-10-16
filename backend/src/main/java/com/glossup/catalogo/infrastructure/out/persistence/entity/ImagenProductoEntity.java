package com.glossup.catalogo.infrastructure.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_imagen")
public class ImagenProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "orden", nullable = false)
    private int orden;

    public ImagenProductoEntity() {}

    public ImagenProductoEntity(ProductoEntity producto, String url, int orden) {
        this.producto = producto;
        this.url = url;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductoEntity getProducto() { return producto; }
    public void setProducto(ProductoEntity producto) { this.producto = producto; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
