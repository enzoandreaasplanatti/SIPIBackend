package org.example.sipibackend.controller;

import jakarta.persistence.EntityManager;
import org.example.sipibackend.entity.Comments;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.CommentDto;
import org.example.sipibackend.repository.CommentsRepository;
import org.example.sipibackend.repository.PublicationsRepository;
import org.example.sipibackend.repository.UserRepository;
import org.example.sipibackend.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationsRepository publicationsRepository;
    @Autowired
    private CommentsService commentsService;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto) {
        User usuario = userRepository.findById(commentDto.getUsuarioId()).orElse(null);
        Publications publicacion = publicationsRepository.findById(commentDto.getPublicacionId()).orElse(null);

        if (usuario == null || publicacion == null) {
            return ResponseEntity.badRequest().body("Usuario o publicación no encontrados");
        }
        // Validar si la publicación permite interacción
        if (publicacion.getAvailabeToRate() == null || !publicacion.getAvailabeToRate()) {
            return ResponseEntity.status(403).body("La publicación no permite comentarios ni calificaciones en este momento.");
        }
        // Validar que el usuario no sea el dueño de la publicación
        if (publicacion.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body("No puedes comentar ni calificar tus propias publicaciones.");
        }
        Comments comment = new Comments();
        comment.setContenido(commentDto.getContenido());
        comment.setCalificacion(commentDto.getCalificacion());
        comment.setUsuario(usuario);
        comment.setPublicacion(publicacion);

        commentsRepository.save(comment);
        entityManager.flush(); // Forzar sincronización con la base de datos

        // --- Lógica de actualización de calificación y contador en Publications ---
        // Obtener todos los comentarios de la publicación directamente del repositorio
        List<Comments> comentarios = commentsRepository.findAllByPublicacionId(publicacion.getId());
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
        publicacion.setCalificacion(nuevoPromedio);
        publicacion.setCalificacionCount(total);
        publicationsRepository.save(publicacion);
        // --- Fin lógica de actualización de calificación ---

        // Crear un DTO para la respuesta y evitar problemas de lazy loading
        CommentDto responseDto = new CommentDto();
        responseDto.setId(comment.getId());
        responseDto.setUsuarioId(usuario.getId());
        responseDto.setPublicacionId(publicacion.getId());
        responseDto.setContenido(comment.getContenido());
        responseDto.setCalificacion(comment.getCalificacion());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/publication/{publicationId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPublication(@PathVariable Long publicationId) {
        List<Comments> comments = commentsService.findByPublicacionId(publicationId);
        // Mapear a DTO para evitar problemas de lazy loading
        List<CommentDto> dtos = comments.stream().map(comment -> {
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setUsuarioId(comment.getUsuario().getId());
            dto.setPublicacionId(comment.getPublicacion().getId());
            dto.setContenido(comment.getContenido());
            dto.setCalificacion(comment.getCalificacion());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }
}
