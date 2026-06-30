package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.LoginRequest;
import com.example.backend_cafedronel.dto.LoginResponse;
import com.example.backend_cafedronel.dto.UsuarioRegistroRequest;
import com.example.backend_cafedronel.dto.UsuarioUpdateRequest;
import com.example.backend_cafedronel.model.Usuario;

import java.util.List;

public interface UsuarioService {

    List<Usuario> listar();

    Usuario obtenerPorId(Integer id);

    LoginResponse autenticar(LoginRequest request);

    Usuario registrar(UsuarioRegistroRequest request);

    Usuario actualizar(Integer id, UsuarioUpdateRequest request);

    void eliminar(Integer id);
}
