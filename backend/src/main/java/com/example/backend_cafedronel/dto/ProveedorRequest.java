package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProveedorRequest {

    @Size(max = 50, message = "El codigo no puede superar 50 caracteres")
    private String codigoProveedor;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotBlank(message = "La categoria es obligatoria")
    @Size(max = 80, message = "La categoria no puede superar 80 caracteres")
    private String categoria;

    @Pattern(regexp = "^\\d{21}$", message = "El RUC debe tener exactamente 21 digitos")
    private String ruc;

    @Size(max = 120, message = "El contacto no puede superar 120 caracteres")
    private String contacto;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
    private String direccion;

    @Size(max = 80, message = "La ciudad no puede superar 80 caracteres")
    private String ciudad;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 255, message = "El correo no puede superar 255 caracteres")
    private String email;

    @Size(max = 255, message = "El sitio web no puede superar 255 caracteres")
    private String sitioWeb;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    private String notas;

    private boolean activo = true;

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
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
