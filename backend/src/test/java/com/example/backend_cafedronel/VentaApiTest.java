package com.example.backend_cafedronel;

import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.ProductoRepository;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class VentaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setNombre("Producto Venta " + UUID.randomUUID());
        producto.setPrecio(7.5);
        producto.setCategoria("bebidas");
        producto.setDescripcion("Producto para venta");
        producto = productoRepository.save(producto);

        usuario = new Usuario();
        usuario.setNombre("Usuario Venta");
        usuario.setEmail("venta." + UUID.randomUUID() + "@test.com");
        usuario.setPassword("hash");
        usuario.setRol("CAJERO");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
    }

    @Test
    void obtenerVentaPorId_existente_devuelve200() throws Exception {
        String body = """
                {
                  "usuarioId": %d,
                  "cantidad": 2,
                  "productoId": %d,
                  "estado": "completado",
                  "metodoPago": "tarjeta"
                }
                """.formatted(usuario.getId(), producto.getId());

        var creada = mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = new ObjectMapper().readTree(creada.getResponse().getContentAsString());
        int id = json.get("id").asInt();

        mockMvc.perform(get("/api/ventas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.total").value(15.0));
    }

    @Test
    void listarVentasPorUsuario_devuelve200() throws Exception {
        mockMvc.perform(get("/api/ventas/usuario/" + usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crearVenta_valida_devuelve201YTotalCalculado() throws Exception {
        String body = """
                {
                  "usuarioId": %d,
                  "cantidad": 4,
                  "productoId": %d,
                  "estado": "completado",
                  "metodoPago": "efectivo"
                }
                """.formatted(usuario.getId(), producto.getId());

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.total").value(30.0))
                .andExpect(jsonPath("$.precioUnitario").value(7.5));
    }

    @Test
    void crearVenta_invalida_devuelve400() throws Exception {
        String body = """
                {
                  "usuarioId": %d,
                  "cantidad": -1,
                  "productoId": %d,
                  "metodoPago": ""
                }
                """.formatted(usuario.getId(), producto.getId());

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void eliminarVenta_inexistente_devuelve404() throws Exception {
        mockMvc.perform(delete("/api/ventas/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
