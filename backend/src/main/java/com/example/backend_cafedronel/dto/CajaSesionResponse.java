package com.example.backend_cafedronel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CajaSesionResponse {

    private Integer id;
    private Integer usuarioId;
    private String usuarioNombre;
    private Double montoInicial;
    private Double totalVentas;
    private Double totalEfectivo;
    private Double efectivoEnCaja;
    private Integer cantidadPedidos;
    private String estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaApertura;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCierre;

    private Double montoCierre;
    private String observaciones;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public Double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(Double montoInicial) { this.montoInicial = montoInicial; }

    public Double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Double totalVentas) { this.totalVentas = totalVentas; }

    public Double getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(Double totalEfectivo) { this.totalEfectivo = totalEfectivo; }

    public Double getEfectivoEnCaja() { return efectivoEnCaja; }
    public void setEfectivoEnCaja(Double efectivoEnCaja) { this.efectivoEnCaja = efectivoEnCaja; }

    public Integer getCantidadPedidos() { return cantidadPedidos; }
    public void setCantidadPedidos(Integer cantidadPedidos) { this.cantidadPedidos = cantidadPedidos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public Double getMontoCierre() { return montoCierre; }
    public void setMontoCierre(Double montoCierre) { this.montoCierre = montoCierre; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
