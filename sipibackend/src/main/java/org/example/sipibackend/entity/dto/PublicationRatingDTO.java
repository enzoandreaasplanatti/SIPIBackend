package org.example.sipibackend.entity.dto;

import lombok.Data;

@Data
public class PublicationRatingDTO {
    private Long publicationId;
    private Integer calificacion;
    private String comentario; // opcional
}

