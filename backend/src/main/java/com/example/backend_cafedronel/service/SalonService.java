package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.SalonMesaResponse;

import java.util.List;

public interface SalonService {

    List<SalonMesaResponse> listarMesas();

    SalonMesaResponse obtenerMesa(Integer numero);

    void ocuparMesa(Integer numero, Integer pedidoId);

    SalonMesaResponse marcarCuenta(Integer numero);

    SalonMesaResponse liberarMesa(Integer numero);

    void liberarPorPedido(Integer pedidoId);

    Integer extraerNumeroMesa(String cliente);
}
