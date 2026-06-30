package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.SalonMesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalonMesaRepository extends JpaRepository<SalonMesa, Integer> {

    List<SalonMesa> findAllByOrderByNumeroAsc();

    Optional<SalonMesa> findByPedido_Id(Integer pedidoId);
}
