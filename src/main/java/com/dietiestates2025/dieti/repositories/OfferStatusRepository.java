package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.OfferState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferStatusRepository extends JpaRepository<OfferState, Integer> {
    // Potrebbe essere utile trovare uno stato per nome
    // Optional<OfferState> findByState(String state);
}