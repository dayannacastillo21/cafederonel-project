package com.example.backend_cafedronel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleAccessApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeConsultarModulosAdministrativos() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reportes/resumen-financiero"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroVeVentasPedidosYProductosPeroNoGestionaCatalogo() throws Exception {
        mockMvc.perform(get("/api/productos/activos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/productos"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "INVENTARIO")
    void inventarioVeStockKardexProveedoresYProductosPeroNoPedidos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventario/movimientos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/pos/checkout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CONTADOR")
    void contadorVeVentasPedidosYReportesPeroNoEditaInventarioNiUsuarios() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reportes/resumen-financiero"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/pedidos/1"))
                .andExpect(status().isForbidden());
    }
}
