package com.example.backend_cafedronel;

import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.Proveedor;
import com.example.backend_cafedronel.repository.InventarioRepository;
import com.example.backend_cafedronel.repository.ProveedorRepository;
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
class InventarioApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Inventario " + UUID.randomUUID());
        proveedor.setTelefono("999222333");
        proveedor.setDireccion("Jr. Inventario 456");
        proveedor.setEmail("inventario." + UUID.randomUUID() + "@test.com");
        proveedor.setActivo(true);
        proveedor = proveedorRepository.save(proveedor);
    }

    @Test
    void obtenerInventarioPorId_existente_devuelve200() throws Exception {
        Inventario item = inventario("Harina test", 10, proveedor);
        item = inventarioRepository.save(item);

        mockMvc.perform(get("/api/inventario/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreInsumo").value("Harina test"));
    }

    @Test
    void listarStockBajo_devuelve200() throws Exception {
        Inventario bajo = inventario("Sal baja", 2, proveedor);
        bajo.setStockMinimo(5);
        inventarioRepository.save(bajo);

        mockMvc.perform(get("/api/inventario/alertas/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crearInventario_valido_devuelve201() throws Exception {
        String body = """
                {
                  "nombreInsumo": "Insumo Test",
                  "cantidad": 12,
                  "unidad": "kg",
                  "stockMinimo": 3,
                  "precioUnitario": 9.5,
                  "proveedor": "%s"
                }
                """.formatted(proveedor.getNombre());

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.proveedor").value(proveedor.getNombre()));
    }

    @Test
    void crearInventario_invalido_devuelve400() throws Exception {
        String body = """
                {
                  "nombreInsumo": "",
                  "cantidad": -1,
                  "unidad": "",
                  "stockMinimo": -1,
                  "precioUnitario": -2,
                  "proveedor": ""
                }
                """;

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void deducirStock_valido_devuelve200YActualizaCantidad() throws Exception {
        Inventario item = inventario("Azucar test", 20, proveedor);
        item = inventarioRepository.save(item);

        String body = "{\"unidades\":5}";
        mockMvc.perform(post("/api/inventario/" + item.getId() + "/deducciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(15));
    }

    @Test
    void registrarMovimientoEntrada_devuelve201YActualizaKardex() throws Exception {
        Inventario item = inventario("Cafe verde test", 10, proveedor);
        item = inventarioRepository.save(item);

        String body = """
                {
                  "tipo": "entrada",
                  "cantidad": 7,
                  "motivo": "Compra de prueba",
                  "referencia": "FAC-TEST"
                }
                """;

        mockMvc.perform(post("/api/inventario/" + item.getId() + "/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("entrada"))
                .andExpect(jsonPath("$.stockAnterior").value(10))
                .andExpect(jsonPath("$.stockNuevo").value(17));

        mockMvc.perform(get("/api/inventario/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(17));

        mockMvc.perform(get("/api/inventario/" + item.getId() + "/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].motivo").value("Compra de prueba"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void eliminarInventario_inexistente_devuelve404() throws Exception {
        mockMvc.perform(delete("/api/inventario/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private static Inventario inventario(String nombre, int cantidad, Proveedor proveedor) {
        Inventario item = new Inventario();
        item.setNombreInsumo(nombre);
        item.setCantidad(cantidad);
        item.setUnidad("kg");
        item.setStockMinimo(2);
        item.setPrecioUnitario(5.5f);
        item.setProveedorEntidad(proveedor);
        return item;
    }
}
