package org.example.sipibackend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String contenido;

    @Column
    private Integer calificacion; // 1 a 5 por ejemplo

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "publicacion_id")
    private Publications publicacion;
}

