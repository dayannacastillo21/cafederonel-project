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
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_sesiones")
public class CajaSesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "total_ventas", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVentas = BigDecimal.ZERO;

    @Column(name = "total_efectivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    @Column(name = "cantidad_pedidos", nullable = false)
    private Integer cantidadPedidos = 0;

    @Column(nullable = false, length = 20)
    private String estado = "abierta";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_cierre", precision = 12, scale = 2)
    private BigDecimal montoCierre;

    @Column(length = 300)
    private String observaciones;

    @PrePersist
    void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        if (montoInicial == null) {
            montoInicial = BigDecimal.ZERO;
        }
        if (totalVentas == null) {
            totalVentas = BigDecimal.ZERO;
        }
        if (totalEfectivo == null) {
            totalEfectivo = BigDecimal.ZERO;
        }
        if (cantidadPedidos == null) {
            cantidadPedidos = 0;
        }
        if (estado == null || estado.isBlank()) {
            estado = "abierta";
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @JsonIgnore
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getUsuarioId() { return usuario != null ? usuario.getId() : null; }

    public Double getMontoInicial() { return toDouble(montoInicial); }
    public void setMontoInicial(Double montoInicial) {
        this.montoInicial = montoInicial != null ? BigDecimal.valueOf(montoInicial) : BigDecimal.ZERO;
    }

    public Double getTotalVentas() { return toDouble(totalVentas); }
    public void setTotalVentas(Double totalVentas) {
        this.totalVentas = totalVentas != null ? BigDecimal.valueOf(totalVentas) : BigDecimal.ZERO;
    }

    public Double getTotalEfectivo() { return toDouble(totalEfectivo); }
    public void setTotalEfectivo(Double totalEfectivo) {
        this.totalEfectivo = totalEfectivo != null ? BigDecimal.valueOf(totalEfectivo) : BigDecimal.ZERO;
    }

    @JsonIgnore
    public BigDecimal getTotalVentasValor() { return totalVentas; }
    @JsonIgnore
    public BigDecimal getTotalEfectivoValor() { return totalEfectivo; }

    public Integer getCantidadPedidos() { return cantidadPedidos; }
    public void setCantidadPedidos(Integer cantidadPedidos) { this.cantidadPedidos = cantidadPedidos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public Double getMontoCierre() { return toDouble(montoCierre); }
    public void setMontoCierre(Double montoCierre) {
        this.montoCierre = montoCierre != null ? BigDecimal.valueOf(montoCierre) : null;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
