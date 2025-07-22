package org.example.sipibackend.service;

import org.example.sipibackend.entity.FavoritePublication;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.Publications;
import java.util.List;

public interface FavoritePublicationService {
    void addFavorite(User user, Publications publication);
    void removeFavorite(User user, Publications publication);
    List<Publications> getFavorites(User user);
    boolean isFavorite(User user, Publications publication);
}

