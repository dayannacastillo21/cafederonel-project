package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByInventario_IdOrderByFechaMovimientoDesc(Integer inventarioId);

    List<MovimientoInventario> findTop50ByOrderByFechaMovimientoDesc();
}
