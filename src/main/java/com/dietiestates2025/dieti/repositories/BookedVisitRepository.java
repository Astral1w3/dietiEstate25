package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.BookedVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedVisitRepository extends JpaRepository<BookedVisit, Integer> {

    // Trova tutte le visite prenotate per un determinato immobile
    List<BookedVisit> findByPropertyIdProperty(Integer propertyId);

}