package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByNombreIgnoreCase(String nombre);
    Optional<Proveedor> findByEmailIgnoreCase(String email);
    Optional<Proveedor> findByRuc(String ruc);
}
