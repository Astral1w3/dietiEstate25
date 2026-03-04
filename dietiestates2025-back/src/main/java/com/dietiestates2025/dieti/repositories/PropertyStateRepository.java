package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.PropertyState;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyStateRepository extends JpaRepository<PropertyState, Integer> {
    /**
     * Trova uno stato della proprietà ignorando maiuscole/minuscole.
     * @param state Il nome dello stato da cercare (es. "available", "OCCUPIED").
     * @return Un Optional contenente lo stato se trovato.
     */
    Optional<PropertyState> findByStateIgnoreCase(String state);
}