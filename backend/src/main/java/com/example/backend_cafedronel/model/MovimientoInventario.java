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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventario_id", nullable = false)
    @JsonIgnore
    private Inventario inventario;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(length = 250)
    private String motivo;

    @Column(length = 120)
    private String referencia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "almacen_id")
    private Integer almacenId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "almacen_id", insertable = false, updatable = false)
    @JsonIgnore
    private Almacen almacenEntidad;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @PrePersist
    void onCreate() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now(LIMA_ZONE);
        }
    }

    @JsonProperty("inventarioId")
    public Integer getInventarioId() {
        return inventario != null ? inventario.getId() : null;
    }

    @JsonProperty("insumo")
    public String getInsumo() {
        return inventario != null ? inventario.getNombreInsumo() : null;
    }

    @JsonProperty("usuarioId")
    public Integer getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @JsonProperty("usuario")
    public String getUsuarioNombre() {
        return usuario != null ? usuario.getNombre() : null;
    }

    @JsonProperty("almacen")
    public String getAlmacen() {
        if (almacenEntidad != null) {
            return almacenEntidad.getNombre();
        }
        return inventario != null ? inventario.getAlmacen() : null;
    }

    @JsonProperty("almacenId")
    public Integer getAlmacenId() { return almacenId; }
    public void setAlmacenId(Integer almacenId) { this.almacenId = almacenId; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Inventario getInventario() { return inventario; }
    public void setInventario(Inventario inventario) { this.inventario = inventario; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }
    public Integer getStockNuevo() { return stockNuevo; }
    public void setStockNuevo(Integer stockNuevo) { this.stockNuevo = stockNuevo; }
    public Float getCostoUnitario() { return costoUnitario != null ? costoUnitario.floatValue() : null; }
    public void setCostoUnitario(Float costoUnitario) {
        this.costoUnitario = costoUnitario != null ? BigDecimal.valueOf(costoUnitario.doubleValue()) : null;
    }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
}
