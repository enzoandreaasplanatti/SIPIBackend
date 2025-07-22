package org.example.sipibackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Publications {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String foto;

    @Column
    private String titulo;

    //Fecha y hora de la publicacion
    @Column
    private LocalDateTime dateTime;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private Double calificacion = 0.0;

    @Column
    private Boolean availabeToRate;

    @Column
    private String direccion;

    @Column(nullable = false)
    private Integer calificacionCount = 0;

    @Embedded
    private FiltersDescription filtersDescription;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;


    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Comments> comentarios = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "publicacion_filtro",
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "filtro_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Filter> filtros = new HashSet<>();


    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<Filter> getFilters() {
        return filtros;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Filter getFiltro() {
        return filtros != null && !filtros.isEmpty() ? filtros.iterator().next() : null;
    }


    public void setFiltro(Filter filtro) {
        if (filtros == null) {
            filtros = new HashSet<>();
        }
        filtros.add(filtro);
    }
}
