package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.model.Proveedor;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> listar();

    Proveedor obtenerPorId(Integer id);

    Proveedor crear(Proveedor proveedor);

    Proveedor actualizar(Integer id, Proveedor proveedor);

    Proveedor cambiarEstado(Integer id, boolean activo);

    void eliminar(Integer id);
}
