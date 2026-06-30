package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.model.Almacen;
import com.example.backend_cafedronel.model.CategoriaProducto;
import com.example.backend_cafedronel.repository.AlmacenRepository;
import com.example.backend_cafedronel.repository.CategoriaProductoRepository;
import com.example.backend_cafedronel.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private final AlmacenRepository almacenRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final InventarioRepository inventarioRepository;

    public CatalogoServiceImpl(
            AlmacenRepository almacenRepository,
            CategoriaProductoRepository categoriaProductoRepository,
            InventarioRepository inventarioRepository) {
        this.almacenRepository = almacenRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Almacen> listarAlmacenesActivos() {
        return almacenRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProducto> listarCategoriasProductoActivas() {
        return categoriaProductoRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarCategoriasInventario() {
        return inventarioRepository.findDistinctCategorias();
    }
}
