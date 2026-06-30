package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {

    List<CategoriaProducto> findByActivoTrueOrderByNombreAsc();
}
