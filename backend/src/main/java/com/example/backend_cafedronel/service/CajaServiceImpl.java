package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.CajaAperturaRequest;
import com.example.backend_cafedronel.dto.CajaCierreRequest;
import com.example.backend_cafedronel.dto.CajaSesionResponse;
import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.CajaSesion;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.repository.CajaSesionRepository;
import com.example.backend_cafedronel.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class CajaServiceImpl implements CajaService {

    private static final Set<String> ROLES_CAJA = Set.of("ADMIN", "CAJERO");
    private static final String ESTADO_ABIERTA = "abierta";
    private static final String ESTADO_CERRADA = "cerrada";

    private final CajaSesionRepository cajaSesionRepository;
    private final UsuarioRepository usuarioRepository;

    public CajaServiceImpl(CajaSesionRepository cajaSesionRepository, UsuarioRepository usuarioRepository) {
        this.cajaSesionRepository = cajaSesionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CajaSesionResponse obtenerActiva(String usuarioEmail) {
        Usuario usuario = obtenerUsuarioAutorizado(usuarioEmail);
        return cajaSesionRepository
                .findFirstByUsuario_IdAndEstadoOrderByFechaAperturaDesc(usuario.getId(), ESTADO_ABIERTA)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public CajaSesionResponse abrir(CajaAperturaRequest request, String usuarioEmail) {
        Usuario usuario = obtenerUsuarioAutorizado(usuarioEmail);

        if (cajaSesionRepository.existsByUsuario_IdAndEstado(usuario.getId(), ESTADO_ABIERTA)) {
            throw new BusinessException("Ya tienes una caja abierta. Cierrala antes de abrir otra.");
        }

        CajaSesion sesion = new CajaSesion();
        sesion.setUsuario(usuario);
        sesion.setMontoInicial(request.getMontoInicial());
        sesion.setEstado(ESTADO_ABIERTA);
        sesion.setFechaApertura(LocalDateTime.now());

        return toResponse(cajaSesionRepository.save(sesion));
    }

    @Override
    @Transactional
    public CajaSesionResponse cerrar(CajaCierreRequest request, String usuarioEmail) {
        Usuario usuario = obtenerUsuarioAutorizado(usuarioEmail);
        CajaSesion sesion = cajaSesionRepository
                .findFirstByUsuario_IdAndEstadoOrderByFechaAperturaDesc(usuario.getId(), ESTADO_ABIERTA)
                .orElseThrow(() -> new BusinessException("No tienes una caja abierta para cerrar."));

        sesion.setEstado(ESTADO_CERRADA);
        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setMontoCierre(request.getMontoCierre());
        sesion.setObservaciones(normalizarObservaciones(request.getObservaciones()));

        return toResponse(cajaSesionRepository.save(sesion));
    }

    @Override
    @Transactional(readOnly = true)
    public CajaSesion obtenerSesionAbiertaObligatoria(Integer usuarioId) {
        return cajaSesionRepository
                .findFirstByUsuario_IdAndEstadoOrderByFechaAperturaDesc(usuarioId, ESTADO_ABIERTA)
                .orElseThrow(() -> new BusinessException(
                        "Debes abrir caja con un monto inicial antes de registrar ventas."));
    }

    @Override
    @Transactional
    public void registrarVenta(Integer cajaSesionId, double total, String metodoPago) {
        CajaSesion sesion = cajaSesionRepository.findById(cajaSesionId)
                .orElseThrow(() -> new ResourceNotFoundException("CajaSesion", cajaSesionId));

        if (!ESTADO_ABIERTA.equalsIgnoreCase(sesion.getEstado())) {
            throw new BusinessException("La caja no esta abierta. Abre caja para continuar vendiendo.");
        }

        BigDecimal monto = BigDecimal.valueOf(total);
        sesion.setTotalVentas(sesion.getTotalVentasValor().add(monto).doubleValue());
        sesion.setCantidadPedidos(sesion.getCantidadPedidos() + 1);

        if (esEfectivo(metodoPago)) {
            sesion.setTotalEfectivo(sesion.getTotalEfectivoValor().add(monto).doubleValue());
        }

        cajaSesionRepository.save(sesion);
    }

    private Usuario obtenerUsuarioAutorizado(String usuarioEmail) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(usuarioEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioEmail));

        String rol = usuario.getRol() == null ? "" : usuario.getRol().trim().toUpperCase();
        if (!ROLES_CAJA.contains(rol)) {
            throw new AccessDeniedException("Solo ADMIN y CAJERO pueden operar la caja");
        }
        return usuario;
    }

    private CajaSesionResponse toResponse(CajaSesion sesion) {
        CajaSesionResponse response = new CajaSesionResponse();
        response.setId(sesion.getId());
        response.setUsuarioId(sesion.getUsuarioId());
        if (sesion.getUsuario() != null) {
            response.setUsuarioNombre(sesion.getUsuario().getNombre());
        }
        response.setMontoInicial(sesion.getMontoInicial());
        response.setTotalVentas(sesion.getTotalVentas());
        response.setTotalEfectivo(sesion.getTotalEfectivo());
        response.setEfectivoEnCaja(
                sesion.getMontoInicial() + (sesion.getTotalEfectivo() != null ? sesion.getTotalEfectivo() : 0));
        response.setCantidadPedidos(sesion.getCantidadPedidos());
        response.setEstado(sesion.getEstado());
        response.setFechaApertura(sesion.getFechaApertura());
        response.setFechaCierre(sesion.getFechaCierre());
        response.setMontoCierre(sesion.getMontoCierre());
        response.setObservaciones(sesion.getObservaciones());
        return response;
    }

    private boolean esEfectivo(String metodoPago) {
        return metodoPago != null && "efectivo".equalsIgnoreCase(metodoPago.trim());
    }

    private String normalizarObservaciones(String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            return null;
        }
        return observaciones.trim();
    }
}
