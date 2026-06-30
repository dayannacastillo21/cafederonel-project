package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotBlank(message = "La categoria es obligatoria")
    @Size(max = 80, message = "La categoria no puede superar 80 caracteres")
    private String categoria;

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
    private String descripcion;

    @Size(max = 50, message = "El SKU no puede superar 50 caracteres")
    private String sku;

    @Size(max = 80, message = "El codigo de barras no puede superar 80 caracteres")
    private String codigoBarras;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private Double costo;

    @PositiveOrZero(message = "El margen no puede ser negativo")
    private Double margenPorcentaje;

    @Size(max = 500, message = "La imagen no puede superar 500 caracteres")
    private String imagenUrl;

    @Size(max = 30, message = "La unidad de venta no puede superar 30 caracteres")
    private String unidadVenta;

    private Boolean activo;

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
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
