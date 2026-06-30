package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.MovimientoInventarioRequest;
import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.MovimientoInventario;

import java.util.List;

public interface InventarioService {

    List<Inventario> listar();

    List<Inventario> buscar(String termino);

    List<Inventario> listarConStockBajo();

    Inventario obtenerPorId(Integer id);

    Inventario crear(Inventario item);

    Inventario actualizar(Integer id, Inventario actualizado);

    Inventario cambiarEstado(Integer id, boolean activo);

    void eliminar(Integer id);

    Inventario deducirStock(Integer id, int unidades);

    List<MovimientoInventario> listarUltimosMovimientos();

    List<MovimientoInventario> listarMovimientos(Integer inventarioId);

    MovimientoInventario registrarMovimiento(Integer inventarioId, MovimientoInventarioRequest request);
}
