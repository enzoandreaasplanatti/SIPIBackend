package org.example.sipibackend.controller;

import org.example.sipibackend.entity.Comments;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.PublicationRatingDTO;
import org.example.sipibackend.entity.dto.UserDTO;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.service.PublicationsService;
import org.example.sipibackend.service.UserService;
import org.example.sipibackend.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/auth/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PublicationsService publicationsService;

    @Autowired
    private CommentsService commentsService;

    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestBody UserDTO userDTO) {
        userService.deactivateUser(userDTO);
        return ResponseEntity.ok("Cuenta desactivada exitosamente");
    }

    // Endpoint para calificar/comentar una publicación
    @PostMapping("/rate-publication")
    public ResponseEntity<?> ratePublication(@RequestBody PublicationRatingDTO ratingDto) {
        Optional<Publications> optionalPublication = publicationsService.findById(ratingDto.getPublicationId());
        if (optionalPublication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada.");
        }
        Publications publication = optionalPublication.get();
        if (!Boolean.TRUE.equals(publication.getAvailabeToRate())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La publicación no está habilitada para calificación/comentario.");
        }
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no encontrado.");
        }
        User user = optionalUser.get();
        // Crear y guardar comentario
        Comments comment = new Comments();
        comment.setContenido(ratingDto.getComentario());
        comment.setCalificacion(ratingDto.getCalificacion());
        comment.setUsuario(user);
        comment.setPublicacion(publication);
        commentsService.save(comment);
        // Actualizar promedio de calificación
        int count = publication.getCalificacionCount() != null ? publication.getCalificacionCount() : 0;
        double actual = publication.getCalificacion() != null ? publication.getCalificacion() : 0.0;
        int nuevaCalificacion = ratingDto.getCalificacion() != null ? ratingDto.getCalificacion() : 0;
        double nuevoPromedio = ((actual * count) + nuevaCalificacion) / (count + 1);
        publication.setCalificacion(nuevoPromedio);
        publication.setCalificacionCount(count + 1);
        publicationsService.save(publication);
        return ResponseEntity.ok("Calificación y comentario registrados correctamente. Promedio actual: " + nuevoPromedio);
    }


}
