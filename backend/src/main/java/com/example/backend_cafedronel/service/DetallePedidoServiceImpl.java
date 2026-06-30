package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.repository.DetallePedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedido> obtenerPorPedido(Integer pedidoId) {
        return detallePedidoRepository.findByPedido_IdOrderByIdAsc(pedidoId);
    }
}
