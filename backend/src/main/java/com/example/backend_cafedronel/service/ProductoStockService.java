package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.ProductoStockDisponibleResponse;
import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.model.ProductoReceta;
import com.example.backend_cafedronel.repository.ProductoRecetaRepository;
import com.example.backend_cafedronel.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductoStockService {

    private final ProductoRepository productoRepository;
    private final ProductoRecetaRepository productoRecetaRepository;

    public ProductoStockService(
            ProductoRepository productoRepository,
            ProductoRecetaRepository productoRecetaRepository) {
        this.productoRepository = productoRepository;
        this.productoRecetaRepository = productoRecetaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoStockDisponibleResponse> listarStockVendible() {
        List<Producto> productos = productoRepository.findAll();
        List<ProductoReceta> recetas = productoRecetaRepository.findByActivoTrue();

        Map<Integer, List<ProductoReceta>> recetasPorProducto = new HashMap<>();
        for (ProductoReceta receta : recetas) {
            Integer productoId = receta.getProducto().getId();
            recetasPorProducto.computeIfAbsent(productoId, ignored -> new ArrayList<>()).add(receta);
        }

        List<ProductoStockDisponibleResponse> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            resultado.add(calcular(producto, recetasPorProducto.get(producto.getId())));
        }
        return resultado;
    }

    private ProductoStockDisponibleResponse calcular(Producto producto, List<ProductoReceta> lineasReceta) {
        ProductoStockDisponibleResponse response = new ProductoStockDisponibleResponse();
        response.setProductoId(producto.getId());

        if (lineasReceta == null || lineasReceta.isEmpty()) {
            response.setSinReceta(true);
            response.setUnidadesDisponibles(null);
            return response;
        }

        int minimo = Integer.MAX_VALUE;
        String limitante = null;

        for (ProductoReceta receta : lineasReceta) {
            Inventario insumo = receta.getInventario();
            int posibles = unidadesDesdeInsumo(insumo.getCantidad(), receta.getCantidadInsumo());
            if (posibles < minimo) {
                minimo = posibles;
                limitante = insumo.getNombreInsumo();
            }
        }

        response.setSinReceta(false);
        response.setUnidadesDisponibles(minimo == Integer.MAX_VALUE ? 0 : minimo);
        response.setInsumoLimitante(limitante);
        return response;
    }

    private int unidadesDesdeInsumo(Integer stock, BigDecimal consumoPorUnidad) {
        if (stock == null || stock <= 0) {
            return 0;
        }
        if (consumoPorUnidad == null || consumoPorUnidad.compareTo(BigDecimal.ZERO) <= 0) {
            return Integer.MAX_VALUE;
        }
        return BigDecimal.valueOf(stock)
                .divide(consumoPorUnidad, 0, RoundingMode.FLOOR)
                .intValue();
    }
}
