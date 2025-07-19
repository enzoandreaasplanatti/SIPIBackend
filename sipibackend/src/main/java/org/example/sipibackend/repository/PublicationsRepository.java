package org.example.sipibackend.repository;

import org.example.sipibackend.entity.Publications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicationsRepository extends JpaRepository<Publications, Long> {

    //Optional<Category> findCategoryById(Long categoryId);
}
