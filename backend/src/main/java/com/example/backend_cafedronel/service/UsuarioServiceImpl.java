package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.LoginRequest;
import com.example.backend_cafedronel.dto.LoginResponse;
import com.example.backend_cafedronel.dto.UsuarioRegistroRequest;
import com.example.backend_cafedronel.dto.UsuarioUpdateRequest;
import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.DuplicateEmailException;
import com.example.backend_cafedronel.exception.InvalidCredentialsException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.CajaSesionRepository;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.repository.VentaRepository;
import com.example.backend_cafedronel.security.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final String DEFAULT_ROLE = "CAJERO";
    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "CAJERO", "INVENTARIO", "CONTADOR");

    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final CajaSesionRepository cajaSesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository,
            CajaSesionRepository cajaSesionRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.cajaSesionRepository = cajaSesionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!Boolean.TRUE.equals(usuario.getActivo())
                || usuario.getPassword() == null
                || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                jwtService.generateToken(usuario.getEmail(), usuario.getRol()));
    }

    @Override
    @Transactional
    public Usuario registrar(UsuarioRegistroRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setEmail(request.getEmail());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevo.setRol(normalizarRol(request.getRol()));
        nuevo.setActivo(true);
        return usuarioRepository.save(nuevo);
    }

    @Override
    @Transactional
    public Usuario actualizar(Integer id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateEmailException(request.getEmail());
                });

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setRol(normalizarRol(request.getRol()));
        usuario.setActivo(request.getActivo());
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        String emailSesion = obtenerEmailSesionActual();
        if (emailSesion != null && emailSesion.equalsIgnoreCase(usuario.getEmail())) {
            throw new BusinessException("No puedes eliminar tu propia cuenta mientras estas conectado.");
        }

        if ("ADMIN".equalsIgnoreCase(usuario.getRol())
                && Boolean.TRUE.equals(usuario.getActivo())
                && contarAdminsActivos(usuario.getId()) == 0) {
            throw new BusinessException("No se puede eliminar el ultimo administrador activo del sistema.");
        }

        if (ventaRepository.existsByUsuario_Id(id) || cajaSesionRepository.existsByUsuario_Id(id)) {
            throw new BusinessException(
                    "No se puede eliminar este usuario porque tiene ventas o sesiones de caja registradas. Inactivalo en su lugar.");
        }

        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private static String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return DEFAULT_ROLE;
        }
        String normalizado = rol.replace("ROLE_", "").trim().toUpperCase();
        if (!ALLOWED_ROLES.contains(normalizado)) {
            throw new BusinessException("Rol no permitido: " + rol);
        }
        return normalizado;
    }

    private String obtenerEmailSesionActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String principal = authentication.getName();
        return principal == null || principal.isBlank() ? null : principal;
    }

    private long contarAdminsActivos(Integer excluirId) {
        return usuarioRepository.findAll().stream()
                .filter(user -> "ADMIN".equalsIgnoreCase(user.getRol()))
                .filter(user -> Boolean.TRUE.equals(user.getActivo()))
                .filter(user -> !user.getId().equals(excluirId))
                .count();
    }
}
