package org.example.sipibackend.service;

import org.example.sipibackend.entity.Comments;

import java.util.List;

public interface CommentsService {
    Comments save(Comments comment);

    List<Comments> findByPublicacionId(Long publicationId);
}
