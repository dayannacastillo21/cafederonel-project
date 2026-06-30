package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByUsuario_IdOrderByFechaVentaDesc(Integer usuarioId);

    List<Venta> findByEstadoIgnoreCaseOrderByFechaVentaDesc(String estado);
}
