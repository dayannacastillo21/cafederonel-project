package com.example.backend_cafedronel.repository;

import com.example.backend_cafedronel.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedido_IdOrderByIdAsc(Integer pedidoId);
}
