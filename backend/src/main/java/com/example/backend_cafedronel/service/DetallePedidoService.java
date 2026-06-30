package com.example.backend_cafedronel.service;

import java.util.List;
import com.example.backend_cafedronel.model.DetallePedido;

public interface DetallePedidoService {
    List<DetallePedido> obtenerPorPedido(Integer pedidoId);
}