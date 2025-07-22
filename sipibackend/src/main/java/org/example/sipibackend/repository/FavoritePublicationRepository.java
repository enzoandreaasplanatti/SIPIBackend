package org.example.sipibackend.repository;

import org.example.sipibackend.entity.FavoritePublication;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.Publications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritePublicationRepository extends JpaRepository<FavoritePublication, Long> {
    Optional<FavoritePublication> findByUserAndPublication(User user, Publications publication);
    List<FavoritePublication> findAllByUser(User user);
}

