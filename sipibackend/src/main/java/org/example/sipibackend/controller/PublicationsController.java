package org.example.sipibackend.controller;

import org.example.sipibackend.entity.Filter;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.PublicationDTO;
import org.example.sipibackend.repository.UserRepository;
import org.example.sipibackend.service.PublicationsService;
import org.example.sipibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/publisher/publications")
public class PublicationsController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationsService publicationService;

   @PostMapping
    public ResponseEntity<?> createPublication(@RequestBody PublicationDTO publicationDto, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty() || !optionalUser.get().getRole().equals("PUBLICADOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para crear publicaciones.");
        }
        User user = optionalUser.get();
        Publications publication = new Publications();
        publication.setTitulo(publicationDto.getTitulo());
        publication.setDescripcion(publicationDto.getDescripcion());
        publication.setFoto(publicationDto.getFoto());
        publication.setDateTime(publicationDto.getDateTime());
        publication.setCalificacion(publicationDto.getCalificacion());
        publication.setAvailabeToRate(publicationDto.getAvailabeToRate());
        publication.setDireccion(publicationDto.getDireccion());
        publication.setUsuario(user);
       if (publicationDto.getFiltroId() != null) {
           Filter filtro = publicationService.findFilterById(publicationDto.getFiltroId());
           publication.setFiltro(filtro);
       }
        Set<Filter> filtros = publicationService.getFiltersByIds(publicationDto.getFiltroIds());
        publication.setFiltros(filtros);
        publicationService.save(publication);
        return ResponseEntity.ok(publication);
    }


    // Endpoint para editar una publicación
    @PostMapping("/{id}/edit")
    public ResponseEntity<?> editPublication(@PathVariable Long id, @RequestBody PublicationDTO updatedDto, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty() || !optionalUser.get().getRole().equals("PUBLICADOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para editar publicaciones.");
        }
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!publication.getUsuario().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes editar publicaciones de otros usuarios.");
        }
        publication.setTitulo(updatedDto.getTitulo());
        publication.setDescripcion(updatedDto.getDescripcion());
        publication.setFoto(updatedDto.getFoto());
        publication.setAvailabeToRate(updatedDto.getAvailabeToRate());
        publication.setCalificacion(updatedDto.getCalificacion());

        Set<Filter> filtros = publicationService.getFiltersByIds(updatedDto.getFiltroIds());
        publication.setFiltros(filtros);
        publicationService.save(publication);
        return ResponseEntity.ok(publication);
    }

    // Endpoint para eliminar una publicación
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> deletePublication(@PathVariable Long id, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty() || !optionalUser.get().getRole().equals("PUBLICADOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar publicaciones.");
        }
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!publication.getUsuario().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar publicaciones de otros usuarios.");
        }
        publicationService.delete(publication);
        return ResponseEntity.ok("Publicación eliminada correctamente.");
    }
}
