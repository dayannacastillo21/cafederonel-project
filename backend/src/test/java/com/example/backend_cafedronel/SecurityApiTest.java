package com.example.backend_cafedronel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void estadoSinToken_devuelve200() throws Exception {
        mockMvc.perform(get("/api/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void endpointProtegidoSinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void endpointProtegidoConTokenInvalido_devuelve401() throws Exception {
        mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Bearer token.invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void preflightAngularLocalhost_devuelveCabecerasCors() throws Exception {
        mockMvc.perform(options("/api/productos")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void usuarioNoPuedeEntrarAAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeEntrarAAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("admin"));
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void usuarioPuedeEntrarAUser() throws Exception {
        mockMvc.perform(get("/api/user/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("user"));
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void usuarioNoPuedeEliminarProductos() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeConsultarProveedorPorId() throws Exception {
        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk());
    }
}
