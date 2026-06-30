package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findAllByOrderByIdDesc();
}
