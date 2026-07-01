package com.example.backend_cafedronel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource({
            "admin@cafedronel.com,ADMIN",
            "caja@cafedronel.com,CAJERO",
            "inventario@cafedronel.com,INVENTARIO",
            "contador@cafedronel.com,CONTADOR"
    })
    void loginValido_devuelveTokenYDatosUsuario(String email, String role) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"password\"}";
        mockMvc.perform(post("/api/auth/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value(role))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginInvalido_devuelve401() throws Exception {
        String body = "{\"email\":\"admin@cafedronel.com\",\"password\":\"incorrecta\"}";
        mockMvc.perform(post("/api/auth/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
