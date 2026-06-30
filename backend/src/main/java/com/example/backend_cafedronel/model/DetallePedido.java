package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Transient
    private Integer pedidoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecio() { return precio != null ? precio.doubleValue() : null; }
    public void setPrecio(Double precio) { this.precio = precio != null ? BigDecimal.valueOf(precio) : null; }

    @JsonIgnore
    public BigDecimal getPrecioValor() { return precio; }
    public void setPrecioValor(BigDecimal precio) { this.precio = precio; }

    public Double getSubtotal() { return subtotal != null ? subtotal.doubleValue() : null; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal != null ? BigDecimal.valueOf(subtotal) : null; }

    @JsonIgnore
    public BigDecimal getSubtotalValor() { return subtotal; }
    public void setSubtotalValor(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Integer getPedidoId() { return pedido != null ? pedido.getId() : pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    @JsonIgnore
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
