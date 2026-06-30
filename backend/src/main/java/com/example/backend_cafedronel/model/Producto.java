package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private Double precio;
    private String categoria;
    private String descripcion;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "codigo_barras", unique = true, length = 80)
    private String codigoBarras;

    @Column(nullable = false)
    private Double costo = 0.0;

    @Column(name = "margen_porcentaje", nullable = false)
    private Double margenPorcentaje = 0.0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "unidad_venta", nullable = false, length = 30)
    private String unidadVenta = "unidad";

    @Column(nullable = false)
    private Boolean activo = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void onCreate() {
        normalizarCamposEmpresariales();
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        normalizarCamposEmpresariales();
        fechaActualizacion = LocalDateTime.now();
    }

    private void normalizarCamposEmpresariales() {
        if (activo == null) {
            activo = true;
        }
        if (sku == null || sku.isBlank()) {
            sku = "CAF-PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (costo == null) {
            costo = 0.0;
        }
        if (margenPorcentaje == null) {
            margenPorcentaje = calcularMargen();
        }
        if (unidadVenta == null || unidadVenta.isBlank()) {
            unidadVenta = "unidad";
        }
    }

    private Double calcularMargen() {
        if (precio == null || precio <= 0 || costo == null || costo <= 0) {
            return 0.0;
        }
        return ((precio - costo) / precio) * 100;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }
    public Double getMargenPorcentaje() { return margenPorcentaje; }
    public void setMargenPorcentaje(Double margenPorcentaje) { this.margenPorcentaje = margenPorcentaje; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getUnidadVenta() { return unidadVenta; }
    public void setUnidadVenta(String unidadVenta) { this.unidadVenta = unidadVenta; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
