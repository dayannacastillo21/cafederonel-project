package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.LoginRequest;
import com.example.backend_cafedronel.dto.LoginResponse;
import com.example.backend_cafedronel.dto.UsuarioRegistroRequest;
import com.example.backend_cafedronel.dto.UsuarioUpdateRequest;
import com.example.backend_cafedronel.exception.DuplicateEmailException;
import com.example.backend_cafedronel.exception.InvalidCredentialsException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import com.example.backend_cafedronel.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final String DEFAULT_ROLE = "CAJERO";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
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
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioRepository.deleteById(id);
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
        return rol.replace("ROLE_", "").trim().toUpperCase();
    }
}
