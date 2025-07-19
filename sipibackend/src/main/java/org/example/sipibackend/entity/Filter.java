package org.example.sipibackend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Filter {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String nombre;

    @ManyToMany(mappedBy = "filtros")
    private Set<Publications> publicaciones = new HashSet<>();
}
