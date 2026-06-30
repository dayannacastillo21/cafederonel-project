package com.example.backend_cafedronel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_proveedor", nullable = false, unique = true, length = 50)
    private String codigoProveedor;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String categoria = "General";

    @Column(length = 21, unique = true)
    private String ruc;

    @Column(length = 120)
    private String contacto;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(length = 80)
    private String ciudad;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "sitio_web", length = 255)
    private String sitioWeb;

    @Column(length = 500)
    private String notas;

    @Column(nullable = false)
    private Boolean activo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void onCreate() {
        normalizarCampos();
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        normalizarCampos();
        fechaActualizacion = LocalDateTime.now();
    }

    private void normalizarCampos() {
        if (activo == null) {
            activo = true;
        }
        if (codigoProveedor == null || codigoProveedor.isBlank()) {
            codigoProveedor = "CAF-PRV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (categoria == null || categoria.isBlank()) {
            categoria = "General";
        }
        if (contacto == null || contacto.isBlank()) {
            contacto = "Area comercial";
        }
        if (ciudad == null || ciudad.isBlank()) {
            ciudad = "Lima";
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoProveedor() { return codigoProveedor; }
    public void setCodigoProveedor(String codigoProveedor) { this.codigoProveedor = codigoProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public boolean isActivo() { return Boolean.TRUE.equals(activo); }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
