package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.MovimientoInventarioRequest;
import com.example.backend_cafedronel.dto.PosCheckoutLineRequest;
import com.example.backend_cafedronel.dto.PosCheckoutRequest;
import com.example.backend_cafedronel.dto.PosCheckoutResponse;
import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.model.ProductoReceta;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.model.Venta;
import com.example.backend_cafedronel.repository.PedidoRepository;
import com.example.backend_cafedronel.repository.ProductoRecetaRepository;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.repository.VentaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PosServiceImpl implements PosService {

    private static final Set<String> ROLES_VENTA = Set.of("ADMIN", "CAJERO");

    private final ProductoService productoService;
    private final ProductoRecetaRepository productoRecetaRepository;
    private final InventarioService inventarioService;
    private final PedidoRepository pedidoRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaService cajaService;
    private final SalonService salonService;

    public PosServiceImpl(
            ProductoService productoService,
            ProductoRecetaRepository productoRecetaRepository,
            InventarioService inventarioService,
            PedidoRepository pedidoRepository,
            VentaRepository ventaRepository,
            UsuarioRepository usuarioRepository,
            CajaService cajaService,
            SalonService salonService) {
        this.productoService = productoService;
        this.productoRecetaRepository = productoRecetaRepository;
        this.inventarioService = inventarioService;
        this.pedidoRepository = pedidoRepository;
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaService = cajaService;
        this.salonService = salonService;
    }

    @Override
    @Transactional
    public PosCheckoutResponse checkout(PosCheckoutRequest request, String cajeroEmail) {
        Usuario cajero = usuarioRepository.findByEmailIgnoreCase(cajeroEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", cajeroEmail));

        String rol = cajero.getRol() == null ? "" : cajero.getRol().trim().toUpperCase();
        if (!ROLES_VENTA.contains(rol)) {
            throw new AccessDeniedException("Solo ADMIN y CAJERO pueden registrar ventas en el POS");
        }

        var cajaSesion = cajaService.obtenerSesionAbiertaObligatoria(cajero.getId());

        Map<Integer, Integer> lineas = fusionarLineas(request.getLineas());
        if (lineas.isEmpty()) {
            throw new BusinessException("El carrito esta vacio");
        }

        List<Producto> productos = new ArrayList<>();
        for (Integer productoId : lineas.keySet()) {
            Producto producto = productoService.obtenerPorId(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", productoId));
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new BusinessException("El producto '" + producto.getNombre() + "' no esta activo");
            }
            productos.add(producto);
        }

        List<ProductoReceta> recetas = productoRecetaRepository.findByProducto_IdInAndActivoTrue(lineas.keySet());
        Map<Integer, List<ProductoReceta>> recetasPorProducto = new HashMap<>();
        for (ProductoReceta receta : recetas) {
            recetasPorProducto
                    .computeIfAbsent(receta.getProducto().getId(), ignored -> new ArrayList<>())
                    .add(receta);
        }

        List<String> productosSinReceta = new ArrayList<>();
        Map<Integer, BigDecimal> consumoPorInsumo = new LinkedHashMap<>();
        Map<Integer, Inventario> insumos = new HashMap<>();

        for (Producto producto : productos) {
            List<ProductoReceta> lineasReceta = recetasPorProducto.get(producto.getId());
            if (lineasReceta == null || lineasReceta.isEmpty()) {
                productosSinReceta.add(producto.getNombre());
                continue;
            }

            int cantidadProducto = lineas.get(producto.getId());
            for (ProductoReceta receta : lineasReceta) {
                Inventario insumo = receta.getInventario();
                insumos.put(insumo.getId(), insumo);
                BigDecimal consumo = receta.getCantidadInsumo()
                        .multiply(BigDecimal.valueOf(cantidadProducto));
                consumoPorInsumo.merge(insumo.getId(), consumo, BigDecimal::add);
            }
        }

        Map<Integer, Integer> deduccionPorInsumo = new LinkedHashMap<>();
        for (Map.Entry<Integer, BigDecimal> entry : consumoPorInsumo.entrySet()) {
            int unidades = convertirAUnidadesEnteras(entry.getValue());
            if (unidades <= 0) {
                continue;
            }
            deduccionPorInsumo.put(entry.getKey(), unidades);
        }

        for (Map.Entry<Integer, Integer> entry : deduccionPorInsumo.entrySet()) {
            Inventario insumo = insumos.get(entry.getKey());
            if (insumo == null) {
                insumo = inventarioService.obtenerPorId(entry.getKey());
            }
            if (insumo.getCantidad() < entry.getValue()) {
                throw new BusinessException(
                        "Stock insuficiente de '" + insumo.getNombreInsumo() + "'. Disponible: "
                                + insumo.getCantidad() + ", requerido: " + entry.getValue());
            }
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(normalizarCliente(request.getCliente()));
        pedido.setEstado(Pedido.EstadoPedido.completado);
        double totalPedido = 0;

        for (Producto producto : productos) {
            int cantidad = lineas.get(producto.getId());
            double subtotal = cantidad * producto.getPrecio();

            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(cantidad);
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            pedido.addDetalle(detalle);
            totalPedido += subtotal;
        }

        pedido.setTotal(totalPedido);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        List<Integer> ventaIds = new ArrayList<>();
        for (DetallePedido detalle : pedidoGuardado.getDetalles()) {
            Venta venta = new Venta();
            venta.setUsuario(cajero);
            venta.setProducto(detalle.getProducto());
            venta.setCantidad(detalle.getCantidad());
            venta.setPrecioUnitario(detalle.getPrecio());
            venta.setTotal(detalle.getSubtotal());
            venta.setMetodoPago(request.getMetodoPago().trim());
            venta.setEstado("completado");
            ventaIds.add(ventaRepository.save(venta).getId());
        }

        String referencia = "POS-PED-" + pedidoGuardado.getId();
        int insumosDescontados = 0;
        for (Map.Entry<Integer, Integer> entry : deduccionPorInsumo.entrySet()) {
            MovimientoInventarioRequest movimiento = new MovimientoInventarioRequest();
            movimiento.setTipo("salida");
            movimiento.setCantidad(entry.getValue());
            movimiento.setMotivo("Venta POS pedido #" + pedidoGuardado.getId());
            movimiento.setReferencia(referencia);
            movimiento.setUsuarioId(cajero.getId());
            inventarioService.registrarMovimiento(entry.getKey(), movimiento);
            insumosDescontados++;
        }

        cajaService.registrarVenta(cajaSesion.getId(), totalPedido, request.getMetodoPago());

        Integer numeroMesa = salonService.extraerNumeroMesa(pedidoGuardado.getCliente());
        if (numeroMesa != null) {
            salonService.ocuparMesa(numeroMesa, pedidoGuardado.getId());
        }

        PosCheckoutResponse response = new PosCheckoutResponse();
        response.setPedidoId(pedidoGuardado.getId());
        response.setPedido(pedidoGuardado);
        response.setVentaIds(ventaIds);
        response.setTotal(totalPedido);
        response.setInsumosDescontados(insumosDescontados);
        response.setProductosSinReceta(productosSinReceta);
        return response;
    }

    private Map<Integer, Integer> fusionarLineas(List<PosCheckoutLineRequest> lineas) {
        Map<Integer, Integer> fusionadas = new LinkedHashMap<>();
        if (lineas == null) {
            return fusionadas;
        }
        for (PosCheckoutLineRequest linea : lineas) {
            if (linea.getProductoId() == null || linea.getCantidad() == null) {
                continue;
            }
            fusionadas.merge(linea.getProductoId(), linea.getCantidad(), Integer::sum);
        }
        return fusionadas;
    }

    private String normalizarCliente(String cliente) {
        if (cliente == null || cliente.isBlank()) {
            return "Mostrador";
        }
        return cliente.trim();
    }

    private int convertirAUnidadesEnteras(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal redondeado = cantidad.setScale(3, RoundingMode.HALF_UP);
        if (redondeado.compareTo(BigDecimal.ONE) < 0) {
            return 1;
        }
        return redondeado.setScale(0, RoundingMode.CEILING).intValue();
    }
}
