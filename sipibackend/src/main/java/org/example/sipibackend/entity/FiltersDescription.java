package org.example.sipibackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.sipibackend.entity.enums.Ciudad;
import org.example.sipibackend.entity.enums.RangoEdad;
import org.example.sipibackend.entity.enums.Zona;

@Data
@Embeddable
public class FiltersDescription {

    @Enumerated(EnumType.STRING)
    private Ciudad ciudad;

    @Enumerated(EnumType.STRING)
    private Zona zona;

    @Enumerated(EnumType.STRING)
    private RangoEdad rangoEdad;

    @Column
    private Boolean eventoPago;    // true = pago, false = gratuito

    @Column
    private Boolean patrocinado;   // true = aparece primero o destacado
}
