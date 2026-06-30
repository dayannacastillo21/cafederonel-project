package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.MovimientoInventarioRequest;
import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.MovimientoInventario;
import com.example.backend_cafedronel.model.Proveedor;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.InventarioRepository;
import com.example.backend_cafedronel.repository.MovimientoInventarioRepository;
import com.example.backend_cafedronel.repository.ProveedorRepository;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class InventarioServiceImpl implements InventarioService {

    private static final Set<String> TIPOS_MOVIMIENTO = Set.of("entrada", "salida", "ajuste", "merma");

    private final InventarioRepository inventarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioServiceImpl(
            InventarioRepository inventarioRepository,
            ProveedorRepository proveedorRepository,
            MovimientoInventarioRepository movimientoRepository,
            UsuarioRepository usuarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> listar() {
        return inventarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listar();
        }
        return inventarioRepository.buscarInventario(termino.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> listarConStockBajo() {
        return inventarioRepository.findConStockBajo();
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario obtenerPorId(Integer id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
    }

    @Override
    @Transactional
    public Inventario crear(Inventario item) {
        item.setId(null);
        item.setProveedorEntidad(resolverProveedor(item.getProveedor()));
        Inventario guardado = inventarioRepository.save(item);
        if (guardado.getCantidad() != null && guardado.getCantidad() > 0) {
            registrarMovimientoInterno(
                    guardado,
                    "entrada",
                    guardado.getCantidad(),
                    0,
                    guardado.getCantidad(),
                    guardado.getPrecioUnitario(),
                    "Stock inicial",
                    "CREACION-INSUMO",
                    null,
                    null);
        }
        return guardado;
    }

    @Override
    @Transactional
    public Inventario actualizar(Integer id, Inventario actualizado) {
        Inventario item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
        int stockAnterior = item.getCantidad();

        item.setNombreInsumo(actualizado.getNombreInsumo());
        item.setCodigoInsumo(actualizado.getCodigoInsumo());
        item.setCategoria(actualizado.getCategoria());
        item.setUbicacion(actualizado.getUbicacion());
        item.setAlmacenId(actualizado.getAlmacenId());
        item.setLote(actualizado.getLote());
        item.setFechaVencimiento(actualizado.getFechaVencimiento());
        item.setCantidad(actualizado.getCantidad());
        item.setUnidad(actualizado.getUnidad());
        item.setStockMinimo(actualizado.getStockMinimo());
        item.setPrecioUnitario(actualizado.getPrecioUnitario());
        item.setProveedorEntidad(resolverProveedor(actualizado.getProveedor()));
        if (actualizado.getActivo() != null) {
            item.setActivo(actualizado.getActivo());
        }

        Inventario guardado = inventarioRepository.save(item);
        if (stockAnterior != guardado.getCantidad()) {
            registrarMovimientoInterno(
                    guardado,
                    "ajuste",
                    Math.abs(guardado.getCantidad() - stockAnterior),
                    stockAnterior,
                    guardado.getCantidad(),
                    guardado.getPrecioUnitario(),
                    "Ajuste por edicion de inventario",
                    "EDICION-INSUMO",
                    null,
                    null);
        }
        return guardado;
    }

    @Override
    @Transactional
    public Inventario cambiarEstado(Integer id, boolean activo) {
        Inventario item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
        item.setActivo(activo);
        return inventarioRepository.save(item);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!inventarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        inventarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Inventario deducirStock(Integer id, int unidades) {
        MovimientoInventarioRequest request = new MovimientoInventarioRequest();
        request.setTipo("salida");
        request.setCantidad(unidades);
        request.setMotivo("Deduccion de stock");
        request.setReferencia("DEDUCCION");
        registrarMovimiento(id, request);
        return obtenerPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarUltimosMovimientos() {
        return movimientoRepository.findTop50ByOrderByFechaMovimientoDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientos(Integer inventarioId) {
        if (!inventarioRepository.existsById(inventarioId)) {
            throw new ResourceNotFoundException("Inventario", inventarioId);
        }
        return movimientoRepository.findByInventario_IdOrderByFechaMovimientoDesc(inventarioId);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarMovimiento(Integer inventarioId, MovimientoInventarioRequest request) {
        Inventario item = obtenerPorId(inventarioId);
        validarInsumoActivo(item);
        String tipo = normalizarTipo(request.getTipo());
        int cantidad = validarCantidad(tipo, request.getCantidad());
        int stockAnterior = item.getCantidad();
        int stockNuevo = calcularStockNuevo(tipo, stockAnterior, cantidad);

        item.setCantidad(stockNuevo);
        Inventario guardado = inventarioRepository.save(item);

        return registrarMovimientoInterno(
                guardado,
                tipo,
                cantidad,
                stockAnterior,
                stockNuevo,
                request.getCostoUnitario(),
                request.getMotivo(),
                request.getReferencia(),
                request.getAlmacenId(),
                resolverUsuario(request.getUsuarioId()));
    }

    private MovimientoInventario registrarMovimientoInterno(
            Inventario item,
            String tipo,
            int cantidad,
            int stockAnterior,
            int stockNuevo,
            Float costoUnitario,
            String motivo,
            String referencia,
            Integer almacenId,
            Usuario usuario) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInventario(item);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setCostoUnitario(costoUnitario);
        movimiento.setMotivo(motivo);
        movimiento.setReferencia(referencia);
        movimiento.setAlmacenId(almacenId != null ? almacenId : item.getAlmacenId());
        movimiento.setUsuario(usuario);
        return movimientoRepository.save(movimiento);
    }

    private int calcularStockNuevo(String tipo, int stockAnterior, int cantidad) {
        return switch (tipo) {
            case "entrada" -> stockAnterior + cantidad;
            case "salida", "merma" -> {
                if (cantidad > stockAnterior) {
                    throw new BusinessException("No hay stock suficiente para registrar la salida");
                }
                yield stockAnterior - cantidad;
            }
            case "ajuste" -> cantidad;
            default -> throw new BusinessException("Tipo de movimiento no valido: " + tipo);
        };
    }

    private int validarCantidad(String tipo, Integer cantidad) {
        if (cantidad == null) {
            throw new BusinessException("La cantidad es obligatoria");
        }
        if ("ajuste".equals(tipo)) {
            if (cantidad < 0) {
                throw new BusinessException("El nuevo stock no puede ser negativo");
            }
            return cantidad;
        }
        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }
        return cantidad;
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }
        String normalizado = tipo.trim().toLowerCase();
        if (!TIPOS_MOVIMIENTO.contains(normalizado)) {
            throw new BusinessException("Tipo de movimiento no valido: " + tipo);
        }
        return normalizado;
    }

    private void validarInsumoActivo(Inventario item) {
        if (Boolean.FALSE.equals(item.getActivo())) {
            throw new BusinessException("El insumo esta inactivo y no admite movimientos de stock");
        }
    }

    private Proveedor resolverProveedor(String nombre) {
        return proveedorRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", nombre));
    }

    private Usuario resolverUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
    }
}
