package org.example.sipibackend.entity.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long usuarioId;
    private Long publicacionId;
    private String contenido;
    private Integer calificacion;
}