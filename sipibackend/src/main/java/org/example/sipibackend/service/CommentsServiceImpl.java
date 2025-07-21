package org.example.sipibackend.service;

import org.example.sipibackend.entity.Comments;
import org.example.sipibackend.repository.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentsRepository commentsRepository;

    @Override
    public Comments save(Comments comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public List<Comments> findByPublicacionId(Long publicationId) {
        return commentsRepository.findAll().stream()
                .filter(comment -> comment.getPublicacion() != null && comment.getPublicacion().getId().equals(publicationId))
                .toList();
    }
}
