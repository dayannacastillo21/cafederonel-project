package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.sql.Timestamp;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    @Column(name = "fecha_venta", nullable = false)
    private Timestamp fechaVenta;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "metodo_pago", length = 40)
    private String metodoPago;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @PrePersist
    void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = new Timestamp(System.currentTimeMillis());
        }
        if (estado == null || estado.isBlank()) {
            estado = "pendiente";
        }
        if (total == null) {
            total = BigDecimal.ZERO;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuario != null ? usuario.getId() : null; }

    public String getUsuarioNombre() {
        return usuario != null ? usuario.getNombre() : null;
    }

    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) {
            this.usuario = null;
            return;
        }
        Usuario referencia = new Usuario();
        referencia.setId(usuarioId);
        this.usuario = referencia;
    }

    @JsonIgnore
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Timestamp getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Timestamp fechaVenta) { this.fechaVenta = fechaVenta; }
    public Double getTotal() { return total != null ? total.doubleValue() : null; }
    public void setTotal(Double total) { this.total = total != null ? BigDecimal.valueOf(total) : null; }
    @JsonIgnore
    public BigDecimal getTotalValor() { return total; }
    public void setTotalValor(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario != null ? precioUnitario.doubleValue() : null; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario != null ? BigDecimal.valueOf(precioUnitario) : null; }
    @JsonIgnore
    public BigDecimal getPrecioUnitarioValor() { return precioUnitario; }
    public void setPrecioUnitarioValor(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
