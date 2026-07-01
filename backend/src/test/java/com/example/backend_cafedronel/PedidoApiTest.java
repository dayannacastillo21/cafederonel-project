package com.example.backend_cafedronel;

import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class PedidoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setNombre("Producto Pedido " + UUID.randomUUID());
        producto.setPrecio(10.0);
        producto.setCategoria("bebidas");
        producto.setDescripcion("Producto para pedido");
        producto = productoRepository.save(producto);
    }

    @Test
    void crearPedido_valido_devuelve201YTotalCalculado() throws Exception {
        String body = """
                {
                  "cliente": "Cliente Pedido",
                  "detalles": [
                    { "productoId": %d, "cantidad": 3 }
                  ]
                }
                """.formatted(producto.getId());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.total").value(30.0))
                .andExpect(jsonPath("$.detalles[0].subtotal").value(30.0));
    }

    @Test
    void crearPedido_invalido_devuelve400() throws Exception {
        String body = """
                {
                  "cliente": "",
                  "detalles": []
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void obtenerPedido_inexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/pedidos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
