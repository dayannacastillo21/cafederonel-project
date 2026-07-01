package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.model.Venta;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.repository.VentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class VentaServiceImpl implements VentaService {

    private static final Set<String> ESTADOS_PERMITIDOS = Set.of("pendiente", "completado", "cancelado", "reembolsado");

    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;

    public VentaServiceImpl(
            ProductoService productoService,
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository) {
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listar() {
        return ventaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaVenta"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorUsuario(Integer usuarioId) {
        if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario", usuarioId);
        }
        return ventaRepository.findByUsuario_IdOrderByFechaVentaDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("El estado es obligatorio");
        }
        String normalizado = estado.trim().toLowerCase();
        if (!ESTADOS_PERMITIDOS.contains(normalizado)) {
            throw new BusinessException("Estado no valido: " + estado);
        }
        return ventaRepository.findByEstadoIgnoreCaseOrderByFechaVentaDesc(normalizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta obtenerPorId(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
    }

    @Override
    @Transactional
    public Venta crear(Venta venta) {
        venta.setId(null);
        completarVenta(venta, venta.getProducto() != null ? venta.getProducto().getId() : null);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Venta actualizar(Integer id, Venta ventaActualizada) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));

        venta.setUsuarioId(ventaActualizada.getUsuarioId());
        venta.setCantidad(ventaActualizada.getCantidad());
        venta.setMetodoPago(ventaActualizada.getMetodoPago());
        venta.setEstado(ventaActualizada.getEstado());

        Integer productoId = ventaActualizada.getProducto() != null ? ventaActualizada.getProducto().getId() : null;
        completarVenta(venta, productoId);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!ventaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta", id);
        }
        ventaRepository.deleteById(id);
    }

    private void completarVenta(Venta venta, Integer productoId) {
        Integer usuarioId = venta.getUsuarioId();
        if (usuarioId == null) {
            throw new BusinessException("Debe indicar el id del usuario");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
        venta.setUsuario(usuario);
        if (venta.getCantidad() == null || venta.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }
        if (productoId == null) {
            throw new BusinessException("Debe indicar el id del producto");
        }

        String estado = venta.getEstado() == null || venta.getEstado().isBlank()
                ? "pendiente"
                : venta.getEstado().trim().toLowerCase();
        if (!ESTADOS_PERMITIDOS.contains(estado)) {
            throw new BusinessException("Estado no valido: " + venta.getEstado());
        }

        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productoId));

        venta.setEstado(estado);
        venta.setProducto(producto);
        venta.setPrecioUnitario(producto.getPrecio());
        venta.setTotal(venta.getCantidad() * producto.getPrecio());
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(new Timestamp(System.currentTimeMillis()));
        }
    }
}
