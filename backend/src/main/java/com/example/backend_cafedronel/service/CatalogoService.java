package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.model.Almacen;
import com.example.backend_cafedronel.model.CategoriaProducto;

import java.util.List;

public interface CatalogoService {

    List<Almacen> listarAlmacenesActivos();

    List<CategoriaProducto> listarCategoriasProductoActivas();

    List<String> listarCategoriasInventario();
}
