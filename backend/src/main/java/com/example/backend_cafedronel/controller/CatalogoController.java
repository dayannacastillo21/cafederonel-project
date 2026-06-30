package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.model.Almacen;
import com.example.backend_cafedronel.model.CategoriaProducto;
import com.example.backend_cafedronel.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/almacenes")
    public ResponseEntity<List<Almacen>> almacenes() {
        return ResponseEntity.ok(catalogoService.listarAlmacenesActivos());
    }

    @GetMapping("/categorias-producto")
    public ResponseEntity<List<CategoriaProducto>> categoriasProducto() {
        return ResponseEntity.ok(catalogoService.listarCategoriasProductoActivas());
    }

    @GetMapping("/categorias-inventario")
    public ResponseEntity<List<String>> categoriasInventario() {
        return ResponseEntity.ok(catalogoService.listarCategoriasInventario());
    }
}
