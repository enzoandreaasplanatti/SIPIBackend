package org.example.sipibackend.service;

import org.example.sipibackend.entity.dto.PublicationFilterDTO;
import org.example.sipibackend.entity.Publications;
import java.util.List;

public interface FilterService {
    List<Publications> filtrarPublicaciones(PublicationFilterDTO filtro);
}
