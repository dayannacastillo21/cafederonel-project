package com.example.backend_cafedronel;

import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioApiTest {

    private static final String EMAIL_PRUEBA = "nuevo.usuario@cafedronel.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.findByEmailIgnoreCase(EMAIL_PRUEBA)
                .ifPresent(usuarioRepository::delete);
    }

    @Test
    void registrarUsuario_noExponePassword() throws Exception {
        String body = """
                {
                  "nombre": "Nuevo Usuario",
                  "email": "nuevo.usuario@cafedronel.com",
                  "password": "abcd1234"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(EMAIL_PRUEBA))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registrarUsuario_invalido_devuelve400() throws Exception {
        String body = """
                {
                  "nombre": "",
                  "email": "correo-invalido",
                  "password": "abc"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorId_noExponePassword() throws Exception {
        Usuario existente = new Usuario();
        existente.setNombre("Consulta Admin");
        existente.setEmail("consulta." + System.nanoTime() + "@cafedronel.com");
        existente.setPassword("hash");
        existente.setRol("CAJERO");
        existente.setActivo(true);
        existente = usuarioRepository.save(existente);

        mockMvc.perform(get("/api/usuarios/" + existente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(existente.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registrarUsuario_emailDuplicado_devuelve409() throws Exception {
        Usuario existente = new Usuario();
        existente.setNombre("Existente");
        existente.setEmail(EMAIL_PRUEBA);
        existente.setPassword("hash");
        existente.setRol("CAJERO");
        existente.setActivo(true);
        usuarioRepository.save(existente);

        String body = """
                {
                  "nombre": "Duplicado",
                  "email": "nuevo.usuario@cafedronel.com",
                  "password": "abcd1234"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
