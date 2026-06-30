package com.example.backend_cafedronel.dto;

public class ProductoStockDisponibleResponse {

    private Integer productoId;
    private Integer unidadesDisponibles;
    private boolean sinReceta;
    private String insumoLimitante;

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public Integer getUnidadesDisponibles() { return unidadesDisponibles; }
    public void setUnidadesDisponibles(Integer unidadesDisponibles) { this.unidadesDisponibles = unidadesDisponibles; }

    public boolean isSinReceta() { return sinReceta; }
    public void setSinReceta(boolean sinReceta) { this.sinReceta = sinReceta; }

    public String getInsumoLimitante() { return insumoLimitante; }
    public void setInsumoLimitante(String insumoLimitante) { this.insumoLimitante = insumoLimitante; }
}
