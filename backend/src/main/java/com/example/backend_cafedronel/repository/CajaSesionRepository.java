package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.CajaSesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CajaSesionRepository extends JpaRepository<CajaSesion, Integer> {

    Optional<CajaSesion> findFirstByUsuario_IdAndEstadoOrderByFechaAperturaDesc(Integer usuarioId, String estado);

    boolean existsByUsuario_IdAndEstado(Integer usuarioId, String estado);

    boolean existsByUsuario_Id(Integer usuarioId);
}
