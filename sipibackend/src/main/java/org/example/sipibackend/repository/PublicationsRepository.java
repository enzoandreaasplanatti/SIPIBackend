package org.example.sipibackend.repository;

import org.example.sipibackend.entity.Publications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PublicationsRepository extends JpaRepository<Publications, Long> {

    @Query("SELECT p FROM Publications p LEFT JOIN FETCH p.filtros WHERE p.id = :id")
    Optional<Publications> findByIdWithFiltros(@Param("id") Long id);

    @Query("SELECT p FROM Publications p LEFT JOIN FETCH p.filtros WHERE p.usuario = :usuario")
    java.util.List<Publications> findAllByUsuarioWithFiltros(@Param("usuario") org.example.sipibackend.entity.User usuario);

    @Query("SELECT p FROM Publications p LEFT JOIN FETCH p.comentarios WHERE p.id = :id")
    Optional<Publications> findByIdWithComentarios(@Param("id") Long id);
}
