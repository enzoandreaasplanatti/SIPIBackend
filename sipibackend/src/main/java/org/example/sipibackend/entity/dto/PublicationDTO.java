package org.example.sipibackend.entity.dto;

import lombok.Data;
import org.example.sipibackend.entity.FiltersDescription;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PublicationDTO {
    private Long id;
    private String foto;
    private String titulo;
    private LocalDateTime dateTime;
    private String descripcion;
    private Double calificacion;
    private Boolean availabeToRate;
    private String direccion;
    private Set<Long> filtroIds;
    private Long usuarioId;

    private Long filtroId;
    private FiltersDescription filtersDescription;

}
