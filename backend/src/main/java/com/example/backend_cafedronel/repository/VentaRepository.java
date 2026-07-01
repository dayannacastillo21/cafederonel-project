package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Venta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @EntityGraph(attributePaths = {"usuario", "producto"})
    @Override
    List<Venta> findAll(Sort sort);

    @EntityGraph(attributePaths = {"usuario", "producto"})
    List<Venta> findByUsuario_IdOrderByFechaVentaDesc(Integer usuarioId);

    boolean existsByUsuario_Id(Integer usuarioId);

    @EntityGraph(attributePaths = {"usuario", "producto"})
    List<Venta> findByEstadoIgnoreCaseOrderByFechaVentaDesc(String estado);
}
