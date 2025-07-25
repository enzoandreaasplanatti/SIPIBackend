package org.example.sipibackend.repository;

import org.example.sipibackend.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllByPublicacionId(Long publicacionId);
}
