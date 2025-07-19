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

    @Column
    private Double calificacion;

    @Column
    private Boolean availabeToRate;

    @Column
    private String direccion;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;


    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL)
    private Set<Comments> comentarios = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "publicacion_filtro",
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "filtro_id")
    )
    private Set<Filter> filtros = new HashSet<>();


    public Set<Filter> getFilters() {
        return filtros;
    }

    public void setFiltro(Filter filtro) {
        if (filtros == null) {
            filtros = new HashSet<>();
        }
        filtros.add(filtro);
    }
}
