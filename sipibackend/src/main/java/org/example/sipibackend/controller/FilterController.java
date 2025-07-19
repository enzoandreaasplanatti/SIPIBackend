package org.example.sipibackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/filtros")
public class FilterController {

    @PostMapping
    public ResponseEntity<String> crearFiltro(@RequestBody String filtro) {
        // Lógica para crear el filtro
        return ResponseEntity.ok("Filtro creado correctamente");
    }
}
