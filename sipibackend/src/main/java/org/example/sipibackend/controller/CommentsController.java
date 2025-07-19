package org.example.sipibackend.controller;

import org.example.sipibackend.entity.Comments;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.CommentDto;
import org.example.sipibackend.repository.CommentsRepository;
import org.example.sipibackend.repository.PublicationsRepository;
import org.example.sipibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationsRepository publicationsRepository;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto) {
        User usuario = userRepository.findById(commentDto.getUsuarioId()).orElse(null);
        Publications publicacion = publicationsRepository.findById(commentDto.getPublicacionId()).orElse(null);

        if (usuario == null || publicacion == null) {
            return ResponseEntity.badRequest().body("Usuario o publicación no encontrados");
        }

        Comments comment = new Comments();
        comment.setContenido(commentDto.getContenido());
        comment.setCalificacion(commentDto.getCalificacion());
        comment.setUsuario(usuario);
        comment.setPublicacion(publicacion);

        commentsRepository.save(comment);

        return ResponseEntity.ok(comment);
    }
}
