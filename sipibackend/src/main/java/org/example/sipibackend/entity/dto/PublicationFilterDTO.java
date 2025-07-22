package org.example.sipibackend.entity.dto;

import lombok.Data;

@Data
public class PublicationFilterDTO {
    private Boolean patrocinado;
    private String zona;
    private String tipoEvento;
    private String modalidad; // en familia, en grupo, etc
    private Boolean matchAll = true; // true = AND, false = OR
}

