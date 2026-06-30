package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    Optional<Producto> findBySkuIgnoreCase(String sku);

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    @Query("select p from Producto p where p.precio >= :min order by p.precio asc")
    List<Producto> buscarConPrecioMinimo(@Param("min") Double min);

    @Query("""
            select p from Producto p
            where lower(p.nombre) like lower(concat('%', :termino, '%'))
               or lower(p.categoria) like lower(concat('%', :termino, '%'))
               or lower(p.sku) like lower(concat('%', :termino, '%'))
               or lower(coalesce(p.codigoBarras, '')) like lower(concat('%', :termino, '%'))
            order by p.nombre asc
            """)
    List<Producto> buscarCatalogo(@Param("termino") String termino);
}
