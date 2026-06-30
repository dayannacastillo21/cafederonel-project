package com.example.backend_cafedronel;

import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.repository.PedidoRepository;
import com.example.backend_cafedronel.service.PedidoServiceImpl;
import com.example.backend_cafedronel.service.ProductoService;
import com.example.backend_cafedronel.service.SalonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private SalonService salonService;

    private PedidoServiceImpl pedidoService;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoServiceImpl(productoService, pedidoRepository, salonService, "4444");
    }

    @Test
    void crearPedido_calculaTotalSegunPrecioCatalogo() {
        Producto catalogo = new Producto();
        catalogo.setId(10);
        catalogo.setNombre("Test");
        catalogo.setPrecio(15.0);
        when(productoService.obtenerPorId(10)).thenReturn(Optional.of(catalogo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido guardado = invocation.getArgument(0);
            guardado.setId(1);
            return guardado;
        });

        Pedido pedido = new Pedido();
        pedido.setCliente("Cliente prueba");
        DetallePedido linea = new DetallePedido();
        linea.setCantidad(4);
        Producto ref = new Producto();
        ref.setId(10);
        linea.setProducto(ref);
        pedido.setDetalles(List.of(linea));

        Pedido creado = pedidoService.crear(pedido);

        assertNotNull(creado.getId());
        assertEquals(60.0, creado.getTotal(), 0.001);
        assertEquals(60.0, creado.getDetalles().get(0).getSubtotal(), 0.001);
    }
}
