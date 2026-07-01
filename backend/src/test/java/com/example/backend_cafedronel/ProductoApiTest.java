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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ProductoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    private Producto productoExistente;

    @BeforeEach
    void setUp() {
        String sufijo = UUID.randomUUID().toString();
        productoExistente = productoRepository.save(producto("Cafe Americano " + sufijo, 8.0, "bebidas"));
        productoRepository.save(producto("Cappuccino " + sufijo, 12.0, "bebidas"));
        productoRepository.save(producto("Croissant " + sufijo, 6.5, "comida"));
    }

    @Test
    void listarProductos_devuelve200YLista() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void obtenerProductoPorId_existente_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/" + productoExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoExistente.getId()))
                .andExpect(jsonPath("$.nombre").exists());
    }

    @Test
    void listarProductosActivos_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void buscarPorPrecioMinimo_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/busqueda/precio-minimo").param("min", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerProductoPorId_inexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/productos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void crearProducto_valido_devuelve201() throws Exception {
        String body = "{\"nombre\":\"Mocaccino\",\"precio\":14.5,\"categoria\":\"bebidas\",\"descripcion\":\"Prueba\"}";
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sku").exists())
                .andExpect(jsonPath("$.nombre").value("Mocaccino"));
    }

    @Test
    void buscarProductosPorTermino_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/busqueda").param("q", productoExistente.getNombre()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void cambiarEstadoProducto_desactivar_devuelveActivoFalse() throws Exception {
        mockMvc.perform(patch("/api/productos/" + productoExistente.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void actualizarProducto_desactivar_devuelveActivoFalse() throws Exception {
        String body = """
                {
                  "nombre": "%s",
                  "precio": %s,
                  "categoria": "bebidas",
                  "activo": false
                }
                """.formatted(productoExistente.getNombre(), productoExistente.getPrecio());

        mockMvc.perform(put("/api/productos/" + productoExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void crearProducto_invalido_devuelve400() throws Exception {
        String body = "{\"nombre\":\"\",\"precio\":-1,\"categoria\":\"\"}";
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    private static Producto producto(String nombre, Double precio, String categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setDescripcion("Producto de prueba");
        return producto;
    }
}
