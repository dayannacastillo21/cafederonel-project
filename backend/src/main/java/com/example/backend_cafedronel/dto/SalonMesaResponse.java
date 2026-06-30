package com.example.backend_cafedronel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SalonMesaResponse {

    private Integer numero;
    private Integer capacidad;
    private Integer posicionX;
    private Integer posicionY;
    private String zona;
    private String forma;
    private String estado;
    private Integer pedidoId;
    private String pedidoCliente;
    private String pedidoEstado;
    private Double pedidoTotal;
    private String pedidoResumen;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    private LocalDateTime pedidoFecha;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    private LocalDateTime actualizadoEn;

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public String getPedidoCliente() { return pedidoCliente; }
    public void setPedidoCliente(String pedidoCliente) { this.pedidoCliente = pedidoCliente; }

    public String getPedidoEstado() { return pedidoEstado; }
    public void setPedidoEstado(String pedidoEstado) { this.pedidoEstado = pedidoEstado; }

    public Double getPedidoTotal() { return pedidoTotal; }
    public void setPedidoTotal(Double pedidoTotal) { this.pedidoTotal = pedidoTotal; }

    public String getPedidoResumen() { return pedidoResumen; }
    public void setPedidoResumen(String pedidoResumen) { this.pedidoResumen = pedidoResumen; }

    public LocalDateTime getPedidoFecha() { return pedidoFecha; }
    public void setPedidoFecha(LocalDateTime pedidoFecha) { this.pedidoFecha = pedidoFecha; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
