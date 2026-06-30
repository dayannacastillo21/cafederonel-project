package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    public ProductoServiceImpl(ProductoRepository productoRepository) { this.productoRepository = productoRepository; }
    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() { return productoRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> porCategoria(String categoria) { return productoRepository.findByCategoriaIgnoreCase(categoria); }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> porPrecioMinimo(Double precioMinimo) {
        return productoRepository.buscarConPrecioMinimo(precioMinimo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listar();
        }
        return productoRepository.buscarCatalogo(termino.trim());
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) { return productoRepository.save(producto); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorId(Integer id) { return productoRepository.findById(id); }
    @Transactional public Producto actualizar(Integer id, Producto actualizado) {
        Producto p = productoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        p.setNombre(actualizado.getNombre()); p.setDescripcion(actualizado.getDescripcion()); p.setPrecio(actualizado.getPrecio()); p.setCategoria(actualizado.getCategoria());
        p.setSku(actualizado.getSku()); p.setCodigoBarras(actualizado.getCodigoBarras()); p.setCosto(actualizado.getCosto()); p.setMargenPorcentaje(actualizado.getMargenPorcentaje());
        p.setImagenUrl(actualizado.getImagenUrl()); p.setUnidadVenta(actualizado.getUnidadVenta());
        if (actualizado.getActivo() != null) { p.setActivo(actualizado.getActivo()); }
        return productoRepository.save(p);
    }

    @Override
    @Transactional
    public Producto cambiarEstado(Integer id, boolean activo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        producto.setActivo(activo);
        return productoRepository.save(producto);
    }

    @Transactional public void eliminar(Integer id) { if(!productoRepository.existsById(id)) throw new ResourceNotFoundException("Producto", id); productoRepository.deleteById(id); }
}
