package com.example.backend_cafedronel.service;

import java.util.List;
import java.util.Optional;
import com.example.backend_cafedronel.model.Pedido;

public interface PedidoService {

    List<Pedido> listar();

    Optional<Pedido> obtenerPorId(Integer id);

    Pedido crear(Pedido pedido);

    Pedido actualizar(Integer id, Pedido pedido);

    Pedido actualizarEstado(Integer id, String estado);

    Pedido actualizarCliente(Integer id, String cliente);

    Pedido cancelar(Integer id);

    Pedido actualizarAdmin(Integer id, Pedido pedido, String adminPin);

    Pedido cancelarAdmin(Integer id, String adminPin);

    void verificarPinAdmin(String adminPin);

    void eliminar(Integer id);
}