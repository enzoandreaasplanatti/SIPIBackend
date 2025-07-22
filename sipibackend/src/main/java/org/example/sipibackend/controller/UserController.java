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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
        // Volver a obtener la publicación con comentarios actualizados (fetch join)
        Publications updatedPublication = publicationsService.findByIdWithComentarios(publication.getId()).orElse(publication);
        java.util.List<Comments> comentarios = updatedPublication.getComentarios() != null ? new java.util.ArrayList<>(updatedPublication.getComentarios()) : new java.util.ArrayList<>();
        System.out.println("Comentarios encontrados: " + comentarios.size());
        int total = 0;
        int suma = 0;
        for (Comments c : comentarios) {
            System.out.println("Comentario ID: " + c.getId() + ", calificacion: " + c.getCalificacion());
            if (c.getCalificacion() != null) {
                suma += c.getCalificacion();
                total++;
            }
        }
        System.out.println("Suma: " + suma + ", Total: " + total);
        double nuevoPromedio = total > 0 ? (double) suma / total : 0.0;
        System.out.println("Nuevo promedio calculado: " + nuevoPromedio);
        updatedPublication.setCalificacion(nuevoPromedio);
        updatedPublication.setCalificacionCount(total);
        publicationsService.save(updatedPublication);
        return ResponseEntity.ok("Calificación y comentario registrados correctamente. Promedio actual: " + nuevoPromedio);
    }

    // PATCH para editar datos del usuario autenticado (excepto rol)
    @PatchMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.updateUserInfo(email, userDTO);
        return ResponseEntity.ok("Datos de usuario actualizados correctamente");
    }


}
