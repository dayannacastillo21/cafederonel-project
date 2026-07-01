package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "inventario")
public class Inventario {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_insumo", nullable = false, length = 150)
    private String nombreInsumo;

    @Column(name = "codigo_insumo", nullable = false, unique = true, length = 50)
    private String codigoInsumo;

    @Column(nullable = false, length = 80)
    private String categoria = "General";

    @Column(nullable = false, length = 120)
    private String ubicacion = "Almacen principal";

    @Column(name = "almacen_id", nullable = false, insertable = true, updatable = true)
    private Integer almacenId = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "almacen_id", insertable = false, updatable = false)
    @JsonIgnore
    private Almacen almacenEntidad;

    @Column(length = 80)
    private String lote;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 30)
    private String unidad;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    @JsonIgnore
    private Proveedor proveedorEntidad;

    @Transient
    private String proveedor;

    @JsonProperty("fechaActualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void onCreate() {
        normalizarCampos();
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now(LIMA_ZONE);
        }
    }

    @PreUpdate
    void onUpdate() {
        normalizarCampos();
        fechaActualizacion = LocalDateTime.now(LIMA_ZONE);
    }

    @PostLoad
    void onLoad() {
        unidad = normalizarUnidad(unidad);
    }

    private void normalizarCampos() {
        if (codigoInsumo == null || codigoInsumo.isBlank()) {
            codigoInsumo = "CAF-INS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (categoria == null || categoria.isBlank()) {
            categoria = "General";
        }
        if (almacenId == null) {
            almacenId = 1;
        }
        if (ubicacion == null || ubicacion.isBlank()) {
            ubicacion = almacenEntidad != null ? almacenEntidad.getNombre() : "Almacen principal";
        }
        if (activo == null) {
            activo = true;
        }
        unidad = normalizarUnidad(unidad);
    }

    private String normalizarUnidad(String value) {
        if (value == null || value.isBlank()) {
            return "unid.";
        }

        String normalized = value.trim().toLowerCase().replaceAll("\\.+$", "");

        if (normalized.startsWith("no identificad")
                || normalized.equals("sin unidad")
                || normalized.equals("sin definir")
                || normalized.equals("n/a")
                || normalized.equals("na")) {
            return "unid.";
        }

        return switch (normalized) {
            case "individuos", "individuo", "unidad", "unidades", "und", "und.", "pieza", "piezas", "u", "uds", "uds." -> "unid.";
            case "unid", "unid." -> "unid.";
            case "kilogramo", "kilogramos", "kilo", "kilos" -> "kg";
            case "litro", "l" -> "litros";
            case "lata" -> "latas";
            case "botella" -> "botellas";
            case "paquete" -> "paquetes";
            case "millar" -> "millares";
            default -> value.trim();
        };
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreInsumo() { return nombreInsumo; }
    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }

    public String getCodigoInsumo() { return codigoInsumo; }
    public void setCodigoInsumo(String codigoInsumo) { this.codigoInsumo = codigoInsumo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Integer getAlmacenId() { return almacenId; }
    public void setAlmacenId(Integer almacenId) { this.almacenId = almacenId; }

    @JsonProperty("almacen")
    public String getAlmacen() {
        return almacenEntidad != null ? almacenEntidad.getNombre() : ubicacion;
    }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getUnidad() {
        return normalizarUnidad(unidad);
    }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Float getPrecioUnitario() {
        return precioUnitario != null ? precioUnitario.floatValue() : null;
    }

    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario != null ? BigDecimal.valueOf(precioUnitario.doubleValue()) : null;
    }

    @JsonIgnore
    public BigDecimal getPrecioUnitarioValor() { return precioUnitario; }
    public void setPrecioUnitarioValor(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getProveedor() {
        return proveedorEntidad != null ? proveedorEntidad.getNombre() : proveedor;
    }

    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    @JsonIgnore
    public Proveedor getProveedorEntidad() { return proveedorEntidad; }
    public void setProveedorEntidad(Proveedor proveedorEntidad) { this.proveedorEntidad = proveedorEntidad; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
