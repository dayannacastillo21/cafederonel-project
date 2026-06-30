package com.example.backend_cafedronel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PosCheckoutRequest {

    @Size(max = 150, message = "El cliente no puede superar 150 caracteres")
    private String cliente;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @Size(max = 40, message = "El metodo de pago no puede superar 40 caracteres")
    private String metodoPago;

    @NotEmpty(message = "El carrito debe incluir al menos un producto")
    @Valid
    private List<PosCheckoutLineRequest> lineas;

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public List<PosCheckoutLineRequest> getLineas() { return lineas; }
    public void setLineas(List<PosCheckoutLineRequest> lineas) { this.lineas = lineas; }
}
