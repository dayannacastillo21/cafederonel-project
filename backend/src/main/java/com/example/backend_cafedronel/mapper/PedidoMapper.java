package com.example.backend_cafedronel.mapper;

import com.example.backend_cafedronel.dto.DetallePedidoLineRequest;
import com.example.backend_cafedronel.dto.PedidoAdminUpdateRequest;
import com.example.backend_cafedronel.dto.PedidoCreateRequest;
import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.Producto;

import java.util.ArrayList;
import java.util.List;

public final class PedidoMapper {

    private PedidoMapper() {
    }

    public static Pedido toPedido(PedidoCreateRequest req) {
        Pedido pedido = new Pedido();
        pedido.setCliente(req.getCliente());
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedidoLineRequest line : req.getDetalles()) {
            DetallePedido d = new DetallePedido();
            d.setCantidad(line.getCantidad());
            Producto ref = new Producto();
            ref.setId(line.getProductoId());
            d.setProducto(ref);
            detalles.add(d);
        }
        pedido.setDetalles(detalles);
        return pedido;
    }

    public static Pedido toPedido(PedidoAdminUpdateRequest req) {
        Pedido pedido = new Pedido();
        pedido.setCliente(req.getCliente());
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedidoLineRequest line : req.getDetalles()) {
            DetallePedido d = new DetallePedido();
            d.setCantidad(line.getCantidad());
            Producto ref = new Producto();
            ref.setId(line.getProductoId());
            d.setProducto(ref);
            detalles.add(d);
        }
        pedido.setDetalles(detalles);
        return pedido;
    }
}
