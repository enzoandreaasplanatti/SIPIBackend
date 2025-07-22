package org.example.sipibackend.service;

import org.example.sipibackend.entity.Filter;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PublicationsService {
    void save(Publications publication);

    Optional<Publications> findById(Long id);

    void delete(Publications publication);

    Set<Filter> getFiltersByIds(Set<Long> filtroIds);

    Filter findFilterById(Long filtroId);

    Set<Filter> findFiltersByPublicationId(Long publicationId);

    List<Publications> findByUsuario(User user);

    Optional<Publications> findByIdWithComentarios(Long id);
}
