package org.example.sipibackend.service;

import org.example.sipibackend.entity.FavoritePublication;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.repository.FavoritePublicationRepository;
import org.example.sipibackend.repository.PublicationsRepository;
import org.example.sipibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoritePublicationServiceImpl implements FavoritePublicationService {
    @Autowired
    private FavoritePublicationRepository favoritePublicationRepository;
    @Autowired
    private PublicationsRepository publicationsRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void addFavorite(User user, Publications publication) {
        if (!favoritePublicationRepository.findByUserAndPublication(user, publication).isPresent()) {
            FavoritePublication fav = new FavoritePublication();
            fav.setUser(user);
            fav.setPublication(publication);
            favoritePublicationRepository.save(fav);
        }
    }

    @Override
    public void removeFavorite(User user, Publications publication) {
        Optional<FavoritePublication> fav = favoritePublicationRepository.findByUserAndPublication(user, publication);
        fav.ifPresent(favoritePublicationRepository::delete);
    }

    @Override
    public List<Publications> getFavorites(User user) {
        return favoritePublicationRepository.findAllByUser(user)
                .stream()
                .map(FavoritePublication::getPublication)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(User user, Publications publication) {
        return favoritePublicationRepository.findByUserAndPublication(user, publication).isPresent();
    }
}

