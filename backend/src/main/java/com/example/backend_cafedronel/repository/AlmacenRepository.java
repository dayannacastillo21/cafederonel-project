package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {

    List<Almacen> findByActivoTrueOrderByNombreAsc();
}
