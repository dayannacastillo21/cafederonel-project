package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.CajaAperturaRequest;
import com.example.backend_cafedronel.dto.CajaCierreRequest;
import com.example.backend_cafedronel.dto.CajaSesionResponse;
import com.example.backend_cafedronel.model.CajaSesion;

public interface CajaService {

    CajaSesionResponse obtenerActiva(String usuarioEmail);

    CajaSesionResponse abrir(CajaAperturaRequest request, String usuarioEmail);

    CajaSesionResponse cerrar(CajaCierreRequest request, String usuarioEmail);

    CajaSesion obtenerSesionAbiertaObligatoria(Integer usuarioId);

    void registrarVenta(Integer cajaSesionId, double total, String metodoPago);
}
