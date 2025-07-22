package org.example.sipibackend.controller;

import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.dto.PublicationFilterDTO;
import org.example.sipibackend.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/filtros")
public class FilterController {

    @Autowired
    private FilterService filterService;

    @PostMapping
    public ResponseEntity<String> crearFiltro(@RequestBody String filtro) {
        return ResponseEntity.ok("Filtro creado correctamente");
    }

    @PostMapping("/publicaciones")
    public ResponseEntity<List<Publications>> filtrarPublicaciones(
            @RequestBody PublicationFilterDTO filtro,
            @RequestParam(defaultValue = "true") Boolean matchAll
    ) {
        filtro.setMatchAll(matchAll);
        List<Publications> publicaciones = filterService.filtrarPublicaciones(filtro);
        return ResponseEntity.ok(publicaciones);
    }
}
