package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.exception.BusinessException;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Proveedor;
import com.example.backend_cafedronel.repository.ProveedorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listar() {
        return proveedorRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor obtenerPorId(Integer id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    @Override
    @Transactional
    public Proveedor crear(Proveedor proveedor) {
        validarEmailUnico(proveedor.getEmail(), null);
        validarRucUnico(proveedor.getRuc(), null);
        proveedor.setId(null);
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public Proveedor actualizar(Integer id, Proveedor actualizado) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));

        validarEmailUnico(actualizado.getEmail(), id);
        validarRucUnico(actualizado.getRuc(), id);

        if (actualizado.getCodigoProveedor() != null && !actualizado.getCodigoProveedor().isBlank()) {
            proveedor.setCodigoProveedor(actualizado.getCodigoProveedor());
        }
        proveedor.setNombre(actualizado.getNombre());
        proveedor.setCategoria(actualizado.getCategoria());
        proveedor.setRuc(actualizado.getRuc());
        proveedor.setContacto(actualizado.getContacto());
        proveedor.setTelefono(actualizado.getTelefono());
        proveedor.setDireccion(actualizado.getDireccion());
        proveedor.setCiudad(actualizado.getCiudad());
        proveedor.setEmail(actualizado.getEmail());
        proveedor.setSitioWeb(actualizado.getSitioWeb());
        proveedor.setNotas(actualizado.getNotas());
        proveedor.setActivo(actualizado.isActivo());
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public Proveedor cambiarEstado(Integer id, boolean activo) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        proveedor.setActivo(activo);
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", id);
        }
        proveedorRepository.deleteById(id);
    }

    private void validarEmailUnico(String email, Integer idExcluido) {
        proveedorRepository.findByEmailIgnoreCase(email)
                .filter(existing -> idExcluido == null || !existing.getId().equals(idExcluido))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un proveedor con el correo " + email);
                });
    }

    private void validarRucUnico(String ruc, Integer idExcluido) {
        if (ruc == null || ruc.isBlank()) {
            return;
        }
        proveedorRepository.findByRuc(ruc)
                .filter(existing -> idExcluido == null || !existing.getId().equals(idExcluido))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un proveedor con el RUC " + ruc);
                });
    }
}
