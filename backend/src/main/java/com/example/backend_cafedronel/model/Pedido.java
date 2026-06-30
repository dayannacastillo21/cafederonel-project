package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({
    "id",
    "cliente",
    "estado",
    "total",
    "fechaCreacion",
    "detalles"
})
@Entity
@Table(name = "pedidos")
public class Pedido {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<DetallePedido> detalles = new ArrayList<>();

    public enum EstadoPedido {
        pendiente, en_proceso, completado, cancelado
    }

    @PrePersist
    void onCreate() {
        if (estado == null) {
            estado = EstadoPedido.pendiente;
        }
        if (total == null) {
            total = BigDecimal.ZERO;
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now(LIMA_ZONE);
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public Double getTotal() { return total != null ? total.doubleValue() : null; }
    public void setTotal(Double total) { this.total = total != null ? BigDecimal.valueOf(total) : null; }

    @JsonIgnore
    public BigDecimal getTotalValor() { return total; }
    public void setTotalValor(BigDecimal total) { this.total = total; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) {
        clearDetalles();
        if (detalles != null) {
            detalles.forEach(this::addDetalle);
        }
    }

    public void addDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    public void clearDetalles() {
        detalles.forEach(detalle -> detalle.setPedido(null));
        detalles.clear();
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
