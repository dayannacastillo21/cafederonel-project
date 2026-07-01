package com.example.backend_cafedronel.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumenFinancieroResponse {
    private Double totalCobrado;
    private Double totalRegistrado;
    private Double ticketPromedio;
    private Integer ventasRegistradas;
    private Integer ventasCompletadas;
    private Integer pedidosRegistrados;
    private Integer pedidosPendientes;
    private Integer pedidosCompletados;
    private Integer pedidosCancelados;
    private Integer cajasAbiertas;
    private Integer cajasCerradas;
    private Double totalVentasCaja;
    private Double totalEfectivoCaja;
    private List<MetodoPagoResumen> cobrosPorMetodo = new ArrayList<>();
    private List<CajaSesionResponse> sesionesCaja = new ArrayList<>();
    private List<CobroResumen> ultimosCobros = new ArrayList<>();

    public Double getTotalCobrado() {
        return totalCobrado;
    }

    public void setTotalCobrado(Double totalCobrado) {
        this.totalCobrado = totalCobrado;
    }

    public Double getTotalRegistrado() {
        return totalRegistrado;
    }

    public void setTotalRegistrado(Double totalRegistrado) {
        this.totalRegistrado = totalRegistrado;
    }

    public Double getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(Double ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public Integer getVentasRegistradas() {
        return ventasRegistradas;
    }

    public void setVentasRegistradas(Integer ventasRegistradas) {
        this.ventasRegistradas = ventasRegistradas;
    }

    public Integer getVentasCompletadas() {
        return ventasCompletadas;
    }

    public void setVentasCompletadas(Integer ventasCompletadas) {
        this.ventasCompletadas = ventasCompletadas;
    }

    public Integer getPedidosRegistrados() {
        return pedidosRegistrados;
    }

    public void setPedidosRegistrados(Integer pedidosRegistrados) {
        this.pedidosRegistrados = pedidosRegistrados;
    }

    public Integer getPedidosPendientes() {
        return pedidosPendientes;
    }

    public void setPedidosPendientes(Integer pedidosPendientes) {
        this.pedidosPendientes = pedidosPendientes;
    }

    public Integer getPedidosCompletados() {
        return pedidosCompletados;
    }

    public void setPedidosCompletados(Integer pedidosCompletados) {
        this.pedidosCompletados = pedidosCompletados;
    }

    public Integer getPedidosCancelados() {
        return pedidosCancelados;
    }

    public void setPedidosCancelados(Integer pedidosCancelados) {
        this.pedidosCancelados = pedidosCancelados;
    }

    public Integer getCajasAbiertas() {
        return cajasAbiertas;
    }

    public void setCajasAbiertas(Integer cajasAbiertas) {
        this.cajasAbiertas = cajasAbiertas;
    }

    public Integer getCajasCerradas() {
        return cajasCerradas;
    }

    public void setCajasCerradas(Integer cajasCerradas) {
        this.cajasCerradas = cajasCerradas;
    }

    public Double getTotalVentasCaja() {
        return totalVentasCaja;
    }

    public void setTotalVentasCaja(Double totalVentasCaja) {
        this.totalVentasCaja = totalVentasCaja;
    }

    public Double getTotalEfectivoCaja() {
        return totalEfectivoCaja;
    }

    public void setTotalEfectivoCaja(Double totalEfectivoCaja) {
        this.totalEfectivoCaja = totalEfectivoCaja;
    }

    public List<MetodoPagoResumen> getCobrosPorMetodo() {
        return cobrosPorMetodo;
    }

    public void setCobrosPorMetodo(List<MetodoPagoResumen> cobrosPorMetodo) {
        this.cobrosPorMetodo = cobrosPorMetodo;
    }

    public List<CajaSesionResponse> getSesionesCaja() {
        return sesionesCaja;
    }

    public void setSesionesCaja(List<CajaSesionResponse> sesionesCaja) {
        this.sesionesCaja = sesionesCaja;
    }

    public List<CobroResumen> getUltimosCobros() {
        return ultimosCobros;
    }

    public void setUltimosCobros(List<CobroResumen> ultimosCobros) {
        this.ultimosCobros = ultimosCobros;
    }
}
