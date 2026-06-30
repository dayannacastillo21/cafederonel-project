package com.example.backend_cafedronel;

import com.example.backend_cafedronel.dto.UsuarioRegistroRequest;
import com.example.backend_cafedronel.exception.DuplicateEmailException;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.security.JwtService;
import com.example.backend_cafedronel.service.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void registrarUsuario_emailDuplicado_lanzaExcepcion() {
        UsuarioServiceImpl servicio = new UsuarioServiceImpl(usuarioRepository, passwordEncoder, jwtService);

        UsuarioRegistroRequest primero = registro("Uno", "mismo@correo.com", "abcd");
        UsuarioRegistroRequest duplicado = registro("Dos", "mismo@correo.com", "efgh");

        when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false, true);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> servicio.registrar(primero));
        assertThrows(DuplicateEmailException.class, () -> servicio.registrar(duplicado));
    }

    private static UsuarioRegistroRequest registro(String nombre, String email, String password) {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        request.setNombre(nombre);
        request.setEmail(email);
        request.setPassword(password);
        request.setRol("usuario");
        return request;
    }
}
