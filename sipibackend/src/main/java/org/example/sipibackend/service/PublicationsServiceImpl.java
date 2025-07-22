package org.example.sipibackend.service;

import org.example.sipibackend.entity.Filter;
import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.repository.FilterRepository;
import org.example.sipibackend.repository.PublicationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PublicationsServiceImpl implements PublicationsService {

    @Autowired
    private PublicationsRepository publicationsRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Override
    public void save(Publications publication) {
        if (publication != null) {
            publicationsRepository.save(publication);
        } else {
            throw new IllegalArgumentException("Publication cannot be null");
        }
    }

    @Override
    public Optional<Publications> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        // Usar fetch join para inicializar filtros y evitar LazyInitializationException
        return publicationsRepository.findByIdWithFiltros(id);
    }

    @Override
    public void delete(Publications publication) {
        if (publication == null || publication.getId() == null) {
            throw new IllegalArgumentException("Publication or its ID cannot be null");
        }
        publicationsRepository.delete(publication);
    }



    @Override
    public Set<Filter> getFiltersByIds(Set<Long> filtroIds) {
        if (filtroIds == null || filtroIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(filterRepository.findAllById(filtroIds));
    }

    @Override
    public Filter findFilterById(Long filtroId) {
        if (filtroId == null) return null;
        return filterRepository.findById(filtroId).orElse(null);
    }

    @Override
    public Set<Filter> findFiltersByPublicationId(Long publicationId) {
        Optional<Publications> pubOpt = publicationsRepository.findById(publicationId);
        return pubOpt.map(Publications::getFiltros).orElse(new HashSet<>());
    }

    @Override
    public java.util.List<Publications> findByUsuario(org.example.sipibackend.entity.User user) {
        if (user == null) return java.util.Collections.emptyList();
        return publicationsRepository.findAllByUsuarioWithFiltros(user);
    }

    @Override
    public Optional<Publications> findByIdWithComentarios(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        // Usar fetch join para inicializar comentarios y evitar LazyInitializationException
        return publicationsRepository.findByIdWithComentarios(id);
    }
}
