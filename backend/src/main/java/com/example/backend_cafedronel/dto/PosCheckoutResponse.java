package com.example.backend_cafedronel.dto;

import com.example.backend_cafedronel.model.Pedido;

import java.util.List;

public class PosCheckoutResponse {

    private Integer pedidoId;
    private Pedido pedido;
    private List<Integer> ventaIds;
    private Double total;
    private int insumosDescontados;
    private List<String> productosSinReceta;

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public List<Integer> getVentaIds() { return ventaIds; }
    public void setVentaIds(List<Integer> ventaIds) { this.ventaIds = ventaIds; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public int getInsumosDescontados() { return insumosDescontados; }
    public void setInsumosDescontados(int insumosDescontados) { this.insumosDescontados = insumosDescontados; }

    public List<String> getProductosSinReceta() { return productosSinReceta; }
    public void setProductosSinReceta(List<String> productosSinReceta) { this.productosSinReceta = productosSinReceta; }
}
