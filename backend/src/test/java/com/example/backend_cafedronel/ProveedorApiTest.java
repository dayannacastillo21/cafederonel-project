package com.example.backend_cafedronel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ProveedorApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarProveedores_devuelve200() throws Exception {
        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerProveedorPorId_existente_devuelve200() throws Exception {
        String email = "detalle." + UUID.randomUUID() + "@test.com";
        String body = """
                {
                  "nombre": "Proveedor Detalle",
                  "categoria": "Insumos retail",
                  "telefono": "999111222",
                  "direccion": "Av. Test 123",
                  "email": "%s",
                  "activo": true
                }
                """.formatted(email);

        MvcResult creado = mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        String json = creado.getResponse().getContentAsString();
        int id = Integer.parseInt(json.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        mockMvc.perform(get("/api/proveedores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void crearProveedor_valido_devuelve201() throws Exception {
        String email = "proveedor." + UUID.randomUUID() + "@test.com";
        String body = """
                {
                  "nombre": "Proveedor Test",
                  "categoria": "Bebidas calientes",
                  "telefono": "999111222",
                  "direccion": "Av. Test 123",
                  "email": "%s",
                  "activo": true
                }
                """.formatted(email);

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void crearProveedor_invalido_devuelve400() throws Exception {
        String body = """
                {
                  "nombre": "",
                  "telefono": "",
                  "direccion": "",
                  "email": "correo-invalido"
                }
                """;

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void eliminarProveedor_inexistente_devuelve404() throws Exception {
        mockMvc.perform(delete("/api/proveedores/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
