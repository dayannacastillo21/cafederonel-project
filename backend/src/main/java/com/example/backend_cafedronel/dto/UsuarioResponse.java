package com.example.backend_cafedronel.dto;

import com.example.backend_cafedronel.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UsuarioResponse {
    private final Integer id;
    private final String nombre;
    private final String email;
    private final String rol;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime fechaRegistro;

    private final Boolean activo;

    private UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol();
        this.fechaRegistro = usuario.getFechaRegistro();
        this.activo = usuario.getActivo();
    }

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario);
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public Boolean getActivo() { return activo; }
}
