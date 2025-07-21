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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    // Endpoint para obtener una publicación por su id
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicationById(@PathVariable Long id, Principal principal) {
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        // Mapear a DTO para evitar problemas de lazy loading
        PublicationDTO responseDto = new PublicationDTO();
        responseDto.setId(publication.getId());
        responseDto.setFoto(publication.getFoto());
        responseDto.setTitulo(publication.getTitulo());
        responseDto.setDateTime(publication.getDateTime());
        responseDto.setDescripcion(publication.getDescripcion());
        responseDto.setCalificacion(publication.getCalificacion());
        responseDto.setAvailabeToRate(publication.getAvailabeToRate());
        responseDto.setDireccion(publication.getDireccion());
        // Asumiendo que tienes métodos para obtener estos valores
        responseDto.setFiltroIds(publication.getFiltros() != null ?
            publication.getFiltros().stream().map(Filter::getId).collect(java.util.stream.Collectors.toSet()) : null);
        responseDto.setFiltroId(publication.getFiltro() != null ? publication.getFiltro().getId() : null);
        responseDto.setFiltersDescription(publication.getFiltersDescription());
        responseDto.setUsuarioId(publication.getUsuario().getId());
        return ResponseEntity.ok(responseDto);
    }

   @PostMapping
    public ResponseEntity<?> createPublication(@RequestBody PublicationDTO publicationDto, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailFromToken = principal.getName();
        String roleFromToken = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_PUBLISHER"))
                .findFirst().orElse(null);

        Optional<User> optionalUser = userRepository.findByEmail(emailFromToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        // Verifica rol y email/id
        if (roleFromToken == null ||
            !(roleFromToken.equals("ROLE_ADMIN") || roleFromToken.equals("ROLE_PUBLISHER")) ||
            !(user.getEmail().equals(emailFromToken) || user.getId().toString().equals(authentication.getPrincipal().toString()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para crear publicaciones.");
        }
        Publications publication = new Publications();
        publication.setTitulo(publicationDto.getTitulo());
        publication.setDescripcion(publicationDto.getDescripcion());
        publication.setFoto(publicationDto.getFoto());
        publication.setDateTime(publicationDto.getDateTime());
        // Inicializa calificacion en null, ignorando el valor del DTO
        publication.setCalificacion(null);
        publication.setAvailabeToRate(publicationDto.getAvailabeToRate());
        publication.setDireccion(publicationDto.getDireccion());
        publication.setUsuario(user);
        // Filtros generales
        if (publicationDto.getFiltroId() != null) {
            Filter filtro = publicationService.findFilterById(publicationDto.getFiltroId());
            publication.setFiltro(filtro);
        }
        Set<Filter> filtros = publicationService.getFiltersByIds(publicationDto.getFiltroIds());
        publication.setFiltros(filtros);
        // Filtros específicos
        if (publicationDto.getFiltersDescription() != null) {
            publication.setFiltersDescription(publicationDto.getFiltersDescription());
        }
        publicationService.save(publication);
        return ResponseEntity.ok(publication);
    }


    // Endpoint para editar una publicación
    @PostMapping("/{id}/edit")
    public ResponseEntity<?> editPublication(@PathVariable Long id, @RequestBody PublicationDTO updatedDto, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailFromToken = principal.getName();
        String roleFromToken = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_PUBLISHER"))
                .findFirst().orElse(null);

        Optional<User> optionalUser = userRepository.findByEmail(emailFromToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        if (roleFromToken == null ||
            !(roleFromToken.equals("ROLE_ADMIN") || roleFromToken.equals("ROLE_PUBLISHER")) ||
            !(user.getEmail().equals(emailFromToken) || user.getId().toString().equals(authentication.getPrincipal().toString()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para editar publicaciones.");
        }
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!publication.getUsuario().getId().equals(user.getId()) && !roleFromToken.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes editar publicaciones de otros usuarios.");
        }
        publication.setTitulo(updatedDto.getTitulo());
        publication.setDescripcion(updatedDto.getDescripcion());
        publication.setFoto(updatedDto.getFoto());
        publication.setAvailabeToRate(updatedDto.getAvailabeToRate());
        // No se permite editar la calificación desde este endpoint
        Set<Filter> filtros = publicationService.getFiltersByIds(updatedDto.getFiltroIds());
        publication.setFiltros(filtros);
        publicationService.save(publication);
        // Mapear a DTO para evitar problemas de lazy loading
        PublicationDTO responseDto = new PublicationDTO();
        responseDto.setId(publication.getId());
        responseDto.setFoto(publication.getFoto());
        responseDto.setTitulo(publication.getTitulo());
        responseDto.setDateTime(publication.getDateTime());
        responseDto.setDescripcion(publication.getDescripcion());
        responseDto.setCalificacion(publication.getCalificacion());
        responseDto.setAvailabeToRate(publication.getAvailabeToRate());
        responseDto.setDireccion(publication.getDireccion());
        responseDto.setFiltroIds(updatedDto.getFiltroIds());
        responseDto.setFiltroId(updatedDto.getFiltroId());
        responseDto.setFiltersDescription(publication.getFiltersDescription());
        responseDto.setUsuarioId(publication.getUsuario().getId());
        return ResponseEntity.ok(responseDto);
    }

    // Endpoint para eliminar una publicación
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> deletePublication(@PathVariable Long id, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailFromToken = principal.getName();
        String roleFromToken = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_PUBLISHER"))
                .findFirst().orElse(null);

        Optional<User> optionalUser = userRepository.findByEmail(emailFromToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        if (roleFromToken == null ||
            !(roleFromToken.equals("ROLE_ADMIN") || roleFromToken.equals("ROLE_PUBLISHER")) ||
            !(user.getEmail().equals(emailFromToken) || user.getId().toString().equals(authentication.getPrincipal().toString()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar publicaciones.");
        }
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!publication.getUsuario().getId().equals(user.getId()) && !roleFromToken.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar publicaciones de otros usuarios.");
        }
        publicationService.delete(publication);
        return ResponseEntity.ok("Publicación eliminada correctamente.");
    }

    // PATCH para habilitar/deshabilitar calificación/comentario
    @PatchMapping("/{id}/interaction")
    public ResponseEntity<?> setPublicationInteraction(@PathVariable Long id, @RequestParam boolean availabeToRate, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailFromToken = principal.getName();
        String roleFromToken = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_PUBLISHER"))
                .findFirst().orElse(null);
        Optional<User> optionalUser = userRepository.findByEmail(emailFromToken);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        if (roleFromToken == null ||
            !(roleFromToken.equals("ROLE_ADMIN") || roleFromToken.equals("ROLE_PUBLISHER")) ||
            !(user.getEmail().equals(emailFromToken) || user.getId().toString().equals(authentication.getPrincipal().toString()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar la interacción de la publicación.");
        }
        Optional<Publications> optionalPublication = publicationService.findById(id);
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!publication.getUsuario().getId().equals(user.getId()) && !roleFromToken.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar publicaciones de otros usuarios.");
        }
        publication.setAvailabeToRate(availabeToRate);
        publicationService.save(publication);
        return ResponseEntity.ok("Interacción actualizada correctamente.");
    }
}
