package org.example.sipibackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FavoritePublication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publications publication;
}