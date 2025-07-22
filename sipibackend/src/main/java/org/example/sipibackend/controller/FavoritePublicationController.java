package org.example.sipibackend.controller;

import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.service.FavoritePublicationService;
import org.example.sipibackend.service.PublicationsService;
import org.example.sipibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/favorite")
public class FavoritePublicationController {
    @Autowired
    private FavoritePublicationService favoritePublicationService;
    @Autowired
    private PublicationsService publicationsService;
    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow();
    }

    @PostMapping("/{publicationId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long publicationId) {
        User user = getAuthenticatedUser();
        Optional<Publications> pubOpt = publicationsService.findById(publicationId);
        if (pubOpt.isEmpty()) return ResponseEntity.notFound().build();
        favoritePublicationService.addFavorite(user, pubOpt.get());
        return ResponseEntity.ok("Agregado a favoritos");
    }

    @DeleteMapping("/{publicationId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long publicationId) {
        User user = getAuthenticatedUser();
        Optional<Publications> pubOpt = publicationsService.findById(publicationId);
        if (pubOpt.isEmpty()) return ResponseEntity.notFound().build();
        favoritePublicationService.removeFavorite(user, pubOpt.get());
        return ResponseEntity.ok("Eliminado de favoritos");
    }

    @GetMapping("/mis-favoritos")
    public ResponseEntity<List<Publications>> getMyFavorites() {
        User user = getAuthenticatedUser();
        List<Publications> favoritos = favoritePublicationService.getFavorites(user);
        // Remover el usuario de cada publicación antes de devolver el JSON
        favoritos.forEach(pub -> pub.setUsuario(null));
        return ResponseEntity.ok(favoritos);
    }
}
