package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Duration VENTANA_EDICION = Duration.ofMinutes(2);
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    private final ProductoService productoService;
    private final PedidoRepository pedidoRepository;
    private final SalonService salonService;
    private final String adminPin;

    public PedidoServiceImpl(
            ProductoService productoService,
            PedidoRepository pedidoRepository,
            SalonService salonService,
            @Value("${app.pedidos.admin-pin}") String adminPin) {
        this.productoService = productoService;
        this.pedidoRepository = pedidoRepository;
        this.salonService = salonService;
        this.adminPin = adminPin;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pedido crear(Pedido pedido) {
        List<DetallePedido> detallesSolicitados = new ArrayList<>(pedido.getDetalles());

        pedido.setId(null);
        pedido.setEstado(Pedido.EstadoPedido.pendiente);
        pedido.clearDetalles();
        poblarDetallesYTotal(pedido, detallesSolicitados);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizar(Integer id, Pedido pedidoActualizado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        validarVentanaEdicion(pedido);

        List<DetallePedido> detallesSolicitados = pedidoActualizado.getDetalles();
        pedido.setCliente(pedidoActualizado.getCliente());
        pedido.clearDetalles();
        poblarDetallesYTotal(pedido, detallesSolicitados);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizarEstado(Integer id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (estado == null || estado.isBlank()) {
            throw new BusinessException("El estado es obligatorio");
        }

        try {
            pedido.setEstado(Pedido.EstadoPedido.valueOf(estado.trim().toLowerCase()));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Estado no valido: " + estado);
        }

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizarCliente(Integer id, String cliente) {
        if (cliente == null || cliente.isBlank()) {
            throw new BusinessException("El cliente o destino es obligatorio");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        validarVentanaEdicion(pedido);
        pedido.setCliente(cliente.trim());

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido cancelar(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        validarVentanaEdicion(pedido);
        pedido.setEstado(Pedido.EstadoPedido.cancelado);

        Pedido cancelado = pedidoRepository.save(pedido);
        salonService.liberarPorPedido(cancelado.getId());
        return cancelado;
    }

    @Override
    @Transactional
    public Pedido actualizarAdmin(Integer id, Pedido pedidoActualizado, String pin) {
        validarPinAdmin(pin);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        List<DetallePedido> detallesSolicitados = pedidoActualizado.getDetalles();
        pedido.setCliente(pedidoActualizado.getCliente());
        if (pedido.getEstado() == Pedido.EstadoPedido.cancelado) {
            pedido.setEstado(Pedido.EstadoPedido.completado);
        }
        pedido.clearDetalles();
        poblarDetallesYTotal(pedido, detallesSolicitados);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido cancelarAdmin(Integer id, String pin) {
        validarPinAdmin(pin);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getEstado() == Pedido.EstadoPedido.cancelado) {
            throw new BusinessException("Este pedido ya fue cancelado");
        }

        pedido.setEstado(Pedido.EstadoPedido.cancelado);
        Pedido cancelado = pedidoRepository.save(pedido);
        salonService.liberarPorPedido(cancelado.getId());
        return cancelado;
    }

    @Override
    @Transactional(readOnly = true)
    public void verificarPinAdmin(String pin) {
        validarPinAdmin(pin);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido", id);
        }
        pedidoRepository.deleteById(id);
    }

    private void validarVentanaEdicion(Pedido pedido) {
        if (pedido.getEstado() == Pedido.EstadoPedido.cancelado) {
            throw new BusinessException("Este pedido ya fue cancelado");
        }

        LocalDateTime limite = pedido.getFechaCreacion().plus(VENTANA_EDICION);
        if (LocalDateTime.now(LIMA_ZONE).isAfter(limite)) {
            throw new BusinessException(
                    "Solo puedes modificar o cancelar el pedido dentro de los 2 minutos posteriores a su creacion");
        }
    }

    private void validarPinAdmin(String pin) {
        if (pin == null || pin.isBlank() || !adminPin.equals(pin.trim())) {
            throw new BusinessException("PIN de administrador incorrecto");
        }
    }

    private void poblarDetallesYTotal(Pedido pedido, List<DetallePedido> detallesSolicitados) {
        if (detallesSolicitados == null || detallesSolicitados.isEmpty()) {
            throw new BusinessException("El pedido debe incluir al menos un detalle");
        }

        Set<Integer> productosUsados = new HashSet<>();
        double total = 0;

        for (DetallePedido solicitado : detallesSolicitados) {
            Integer productoId = extraerProductoId(solicitado);
            if (!productosUsados.add(productoId)) {
                throw new BusinessException("El producto " + productoId + " esta duplicado en el pedido");
            }
            if (solicitado.getCantidad() == null || solicitado.getCantidad() <= 0) {
                throw new BusinessException("La cantidad debe ser mayor que cero");
            }

            Producto producto = resolverProducto(productoId);
            double subtotal = solicitado.getCantidad() * producto.getPrecio();

            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(solicitado.getCantidad());
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            pedido.addDetalle(detalle);

            total += subtotal;
        }

        pedido.setTotal(total);
    }

    private Integer extraerProductoId(DetallePedido detalle) {
        if (detalle == null || detalle.getProducto() == null || detalle.getProducto().getId() == null) {
            throw new BusinessException("Cada detalle debe referenciar un producto por id");
        }
        return detalle.getProducto().getId();
    }

    private Producto resolverProducto(Integer id) {
        return productoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
