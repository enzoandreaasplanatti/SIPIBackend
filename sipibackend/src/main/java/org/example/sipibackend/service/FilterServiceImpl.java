package org.example.sipibackend.service;

import org.example.sipibackend.entity.Publications;
import org.example.sipibackend.entity.dto.PublicationFilterDTO;
import org.example.sipibackend.repository.FilterRepository;
import org.example.sipibackend.repository.PublicationsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilterServiceImpl implements FilterService {
    @Autowired
    private FilterRepository filterRepository;
    @Autowired
    private PublicationsRepository publicationsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Publications> filtrarPublicaciones(PublicationFilterDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Publications> cq = cb.createQuery(Publications.class);
        Root<Publications> root = cq.from(Publications.class);
        List<Predicate> predicates = new ArrayList<>();

        // Filtros sobre Publications.filtersDescription
        Path<?> filtersDescription = root.get("filtersDescription");
        if (filtro.getPatrocinado() != null)
            predicates.add(cb.equal(filtersDescription.get("patrocinado"), filtro.getPatrocinado()));
        if (filtro.getZona() != null)
            predicates.add(cb.equal(filtersDescription.get("zona"), filtro.getZona()));
        if (filtro.getModalidad() != null)
            predicates.add(cb.equal(filtersDescription.get("tipoEvento"), filtro.getModalidad()));

        // Filtro sobre la entidad Filter (ManyToMany) solo para tipoEvento
        if (filtro.getTipoEvento() != null) {
            Join<Object, Object> filtrosJoin = root.join("filtros", JoinType.LEFT);
            predicates.add(cb.equal(filtrosJoin.get("tipoEvento"), filtro.getTipoEvento()));
            cq.distinct(true); // Para evitar duplicados por el join
        }

        if (predicates.isEmpty()) {
            cq.select(root);
        } else if (Boolean.TRUE.equals(filtro.getMatchAll())) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        } else {
            cq.where(cb.or(predicates.toArray(new Predicate[0])));
        }
        return entityManager.createQuery(cq).getResultList();
    }
}
