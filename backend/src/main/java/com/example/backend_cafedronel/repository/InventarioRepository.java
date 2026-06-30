package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    @Query("select i from Inventario i where i.activo = true and i.cantidad <= i.stockMinimo order by i.id asc")
    List<Inventario> findConStockBajo();

    @Query("""
            select i from Inventario i
            where lower(i.nombreInsumo) like lower(concat('%', :termino, '%'))
               or lower(i.codigoInsumo) like lower(concat('%', :termino, '%'))
               or lower(i.categoria) like lower(concat('%', :termino, '%'))
               or lower(i.ubicacion) like lower(concat('%', :termino, '%'))
               or lower(coalesce(i.lote, '')) like lower(concat('%', :termino, '%'))
               or lower(i.proveedorEntidad.nombre) like lower(concat('%', :termino, '%'))
            order by i.nombreInsumo asc
            """)
    List<Inventario> buscarInventario(@Param("termino") String termino);

    @Query("select distinct i.categoria from Inventario i where i.categoria is not null and trim(i.categoria) <> '' order by i.categoria asc")
    List<String> findDistinctCategorias();
}
