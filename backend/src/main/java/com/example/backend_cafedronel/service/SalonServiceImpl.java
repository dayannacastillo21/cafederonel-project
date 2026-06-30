package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.SalonMesaResponse;
import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.SalonMesa;
import com.example.backend_cafedronel.repository.PedidoRepository;
import com.example.backend_cafedronel.repository.SalonMesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalonServiceImpl implements SalonService {

    private final SalonMesaRepository salonMesaRepository;
    private final PedidoRepository pedidoRepository;

    public SalonServiceImpl(SalonMesaRepository salonMesaRepository, PedidoRepository pedidoRepository) {
        this.salonMesaRepository = salonMesaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalonMesaResponse> listarMesas() {
        return salonMesaRepository.findAllByOrderByNumeroAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SalonMesaResponse obtenerMesa(Integer numero) {
        return toResponse(buscarMesa(numero));
    }

    @Override
    @Transactional
    public void ocuparMesa(Integer numero, Integer pedidoId) {
        SalonMesa mesa = buscarMesa(numero);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", pedidoId));

        mesa.setEstado(SalonMesa.EstadoMesa.ocupada);
        mesa.setPedido(pedido);
        salonMesaRepository.save(mesa);
    }

    @Override
    @Transactional
    public SalonMesaResponse marcarCuenta(Integer numero) {
        SalonMesa mesa = buscarMesa(numero);
        if (mesa.getEstado() == SalonMesa.EstadoMesa.disponible) {
            throw new BusinessException("La mesa " + numero + " no tiene un pedido activo");
        }
        mesa.setEstado(SalonMesa.EstadoMesa.cuenta);
        return toResponse(salonMesaRepository.save(mesa));
    }

    @Override
    @Transactional
    public SalonMesaResponse liberarMesa(Integer numero) {
        SalonMesa mesa = buscarMesa(numero);
        mesa.setEstado(SalonMesa.EstadoMesa.disponible);
        mesa.setPedido(null);
        return toResponse(salonMesaRepository.save(mesa));
    }

    @Override
    @Transactional
    public void liberarPorPedido(Integer pedidoId) {
        salonMesaRepository.findByPedido_Id(pedidoId).ifPresent(mesa -> {
            mesa.setEstado(SalonMesa.EstadoMesa.disponible);
            mesa.setPedido(null);
            salonMesaRepository.save(mesa);
        });
    }

    @Override
    public Integer extraerNumeroMesa(String cliente) {
        if (cliente == null || !cliente.startsWith("Mesa ")) {
            return null;
        }
        try {
            int numero = Integer.parseInt(cliente.substring(5).trim());
            return numero >= 1 && numero <= 10 ? numero : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private SalonMesa buscarMesa(Integer numero) {
        return salonMesaRepository.findById(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa", numero));
    }

    private SalonMesaResponse toResponse(SalonMesa mesa) {
        SalonMesaResponse response = new SalonMesaResponse();
        response.setNumero(mesa.getNumero());
        response.setCapacidad(mesa.getCapacidad());
        response.setPosicionX(mesa.getPosicionX());
        response.setPosicionY(mesa.getPosicionY());
        response.setZona(mesa.getZona());
        response.setForma(mesa.getForma());
        response.setEstado(mesa.getEstado().name());
        response.setActualizadoEn(mesa.getActualizadoEn());

        Pedido pedido = mesa.getPedido();
        if (pedido != null) {
            response.setPedidoId(pedido.getId());
            response.setPedidoCliente(pedido.getCliente());
            response.setPedidoEstado(pedido.getEstado().name());
            response.setPedidoTotal(pedido.getTotal());
            response.setPedidoFecha(pedido.getFechaCreacion());
            response.setPedidoResumen(resumirPedido(pedido));
        }

        return response;
    }

    private String resumirPedido(Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            return "Sin productos";
        }
        return pedido.getDetalles().stream()
                .map(this::resumirDetalle)
                .collect(Collectors.joining(" · "));
    }

    private String resumirDetalle(DetallePedido detalle) {
        String nombre = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
        return detalle.getCantidad() + "x " + nombre;
    }
}
