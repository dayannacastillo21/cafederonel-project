package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.ProductoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductoRecetaRepository extends JpaRepository<ProductoReceta, Integer> {

    List<ProductoReceta> findByProducto_IdInAndActivoTrue(Collection<Integer> productoIds);

    @Query("""
            SELECT pr FROM ProductoReceta pr
            JOIN FETCH pr.producto
            JOIN FETCH pr.inventario
            WHERE pr.activo = true
            """)
    List<ProductoReceta> findByActivoTrue();
}
