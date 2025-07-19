package org.example.sipibackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FiltersDescription {

    @Column
    private String ciudad;         // Ej: "CABA", "Rosario"

    @Column
    private String zona;           // Ej: "Palermo", "Centro"

    @Column
    private String rangoEdad;      // Ej: "18-25", "todas", "mayores de 60"

    @Column
    private String tipoEvento;     // Ej: "gastronómico", "cultural", "música"

    @Column
    private Boolean eventoPago;    // true = pago, false = gratuito

    @Column
    private Boolean patrocinado;   // true = aparece primero o destacado
}
