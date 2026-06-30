package com.example.backend_cafedronel.service;

import java.util.List;
import com.example.backend_cafedronel.model.Venta;

public interface VentaService {

    List<Venta> listar();

    List<Venta> listarPorUsuario(Integer usuarioId);

    List<Venta> listarPorEstado(String estado);

    Venta obtenerPorId(Integer id);

    Venta crear(Venta venta);

    Venta actualizar(Integer id, Venta venta);

    void eliminar(Integer id);
}