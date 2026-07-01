package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.CajaSesionResponse;
import com.example.backend_cafedronel.dto.CobroResumen;
import com.example.backend_cafedronel.dto.MetodoPagoResumen;
import com.example.backend_cafedronel.dto.ResumenFinancieroResponse;
import com.example.backend_cafedronel.model.CajaSesion;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.Venta;
import com.example.backend_cafedronel.repository.CajaSesionRepository;
import com.example.backend_cafedronel.repository.PedidoRepository;
import com.example.backend_cafedronel.repository.VentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final String ESTADO_COMPLETADO = "completado";

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final CajaSesionRepository cajaSesionRepository;

    public ReporteServiceImpl(
            VentaRepository ventaRepository,
            PedidoRepository pedidoRepository,
            CajaSesionRepository cajaSesionRepository) {
        this.ventaRepository = ventaRepository;
        this.pedidoRepository = pedidoRepository;
        this.cajaSesionRepository = cajaSesionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenFinancieroResponse resumenFinanciero() {
        List<Venta> ventas = ventaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaVenta"));
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByIdDesc();
        List<CajaSesion> sesiones = cajaSesionRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaApertura"));

        ResumenFinancieroResponse response = new ResumenFinancieroResponse();
        response.setVentasRegistradas(ventas.size());
        response.setVentasCompletadas((int) ventas.stream().filter(this::esVentaCompletada).count());
        response.setTotalRegistrado(redondear(ventas.stream().mapToDouble(this::totalVenta).sum()));
        response.setTotalCobrado(redondear(ventas.stream()
                .filter(this::esVentaCompletada)
                .mapToDouble(this::totalVenta)
                .sum()));
        response.setTicketPromedio(response.getVentasCompletadas() > 0
                ? redondear(response.getTotalCobrado() / response.getVentasCompletadas())
                : 0);

        response.setPedidosRegistrados(pedidos.size());
        response.setPedidosPendientes(contarPedidos(pedidos, Pedido.EstadoPedido.pendiente));
        response.setPedidosCompletados(contarPedidos(pedidos, Pedido.EstadoPedido.completado));
        response.setPedidosCancelados(contarPedidos(pedidos, Pedido.EstadoPedido.cancelado));

        response.setCajasAbiertas((int) sesiones.stream().filter(s -> "abierta".equalsIgnoreCase(s.getEstado())).count());
        response.setCajasCerradas((int) sesiones.stream().filter(s -> "cerrada".equalsIgnoreCase(s.getEstado())).count());
        response.setTotalVentasCaja(redondear(sesiones.stream()
                .mapToDouble(s -> valor(s.getTotalVentas()))
                .sum()));
        response.setTotalEfectivoCaja(redondear(sesiones.stream()
                .mapToDouble(s -> valor(s.getTotalEfectivo()))
                .sum()));
        response.setCobrosPorMetodo(cobrosPorMetodo(ventas));
        response.setSesionesCaja(sesiones.stream().map(this::toCajaResponse).toList());
        response.setUltimosCobros(ventas.stream()
                .sorted(Comparator.comparing(this::fechaVentaSafe).reversed())
                .limit(10)
                .map(this::toCobroResumen)
                .toList());

        return response;
    }

    private List<MetodoPagoResumen> cobrosPorMetodo(List<Venta> ventas) {
        Map<String, MetodoPagoResumen> porMetodo = new LinkedHashMap<>();
        for (Venta venta : ventas) {
            if (!esVentaCompletada(venta)) {
                continue;
            }
            String metodo = normalizarMetodoPago(venta.getMetodoPago());
            MetodoPagoResumen resumen = porMetodo.computeIfAbsent(metodo, key -> {
                MetodoPagoResumen nuevo = new MetodoPagoResumen();
                nuevo.setMetodoPago(key);
                nuevo.setCantidad(0);
                nuevo.setTotal(0.0);
                return nuevo;
            });
            resumen.setCantidad(resumen.getCantidad() + 1);
            resumen.setTotal(redondear(resumen.getTotal() + totalVenta(venta)));
        }
        return porMetodo.values().stream().toList();
    }

    private CobroResumen toCobroResumen(Venta venta) {
        CobroResumen resumen = new CobroResumen();
        resumen.setVentaId(venta.getId());
        resumen.setProducto(venta.getProducto() != null ? venta.getProducto().getNombre() : "Producto");
        resumen.setUsuarioId(venta.getUsuarioId());
        resumen.setUsuarioNombre(venta.getUsuarioNombre());
        resumen.setCantidad(venta.getCantidad());
        resumen.setMetodoPago(normalizarMetodoPago(venta.getMetodoPago()));
        resumen.setEstado(venta.getEstado());
        resumen.setTotal(totalVenta(venta));
        resumen.setFechaVenta(venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : null);
        return resumen;
    }

    private CajaSesionResponse toCajaResponse(CajaSesion sesion) {
        CajaSesionResponse response = new CajaSesionResponse();
        response.setId(sesion.getId());
        response.setUsuarioId(sesion.getUsuarioId());
        if (sesion.getUsuario() != null) {
            response.setUsuarioNombre(sesion.getUsuario().getNombre());
        }
        response.setMontoInicial(sesion.getMontoInicial());
        response.setTotalVentas(sesion.getTotalVentas());
        response.setTotalEfectivo(sesion.getTotalEfectivo());
        response.setEfectivoEnCaja(valor(sesion.getMontoInicial()) + valor(sesion.getTotalEfectivo()));
        response.setCantidadPedidos(sesion.getCantidadPedidos());
        response.setEstado(sesion.getEstado());
        response.setFechaApertura(sesion.getFechaApertura());
        response.setFechaCierre(sesion.getFechaCierre());
        response.setMontoCierre(sesion.getMontoCierre());
        response.setObservaciones(sesion.getObservaciones());
        return response;
    }

    private int contarPedidos(List<Pedido> pedidos, Pedido.EstadoPedido estado) {
        return (int) pedidos.stream().filter(pedido -> pedido.getEstado() == estado).count();
    }

    private boolean esVentaCompletada(Venta venta) {
        return venta.getEstado() != null && ESTADO_COMPLETADO.equalsIgnoreCase(venta.getEstado());
    }

    private double totalVenta(Venta venta) {
        return valor(venta.getTotal());
    }

    private double valor(Double value) {
        return value != null ? value : 0;
    }

    private String normalizarMetodoPago(String metodoPago) {
        return metodoPago == null || metodoPago.isBlank() ? "sin metodo" : metodoPago.trim().toLowerCase();
    }

    private Timestamp fechaVentaSafe(Venta venta) {
        return venta.getFechaVenta() != null ? venta.getFechaVenta() : new Timestamp(0);
    }

    private double redondear(double value) {
        return Math.round((value + Math.ulp(value)) * 100.0) / 100.0;
    }
}
