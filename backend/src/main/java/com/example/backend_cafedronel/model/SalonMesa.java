package com.example.backend_cafedronel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "salon_mesas")
public class SalonMesa {

    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    @Id
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad = 4;

    @Column(name = "posicion_x", nullable = false)
    private Integer posicionX = 0;

    @Column(name = "posicion_y", nullable = false)
    private Integer posicionY = 0;

    @Column(nullable = false, length = 30)
    private String zona = "salon";

    @Column(nullable = false, length = 20)
    private String forma = "cuadrada";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado = EstadoMesa.disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    public enum EstadoMesa {
        disponible, ocupada, cuenta, reservada, bloqueada
    }

    @PrePersist
    @PreUpdate
    void touch() {
        actualizadoEn = LocalDateTime.now(LIMA_ZONE);
    }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public Integer getPosicionX() { return posicionX; }
    public void setPosicionX(Integer posicionX) { this.posicionX = posicionX; }

    public Integer getPosicionY() { return posicionY; }
    public void setPosicionY(Integer posicionY) { this.posicionY = posicionY; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getForma() { return forma; }
    public void setForma(String forma) { this.forma = forma; }

    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
