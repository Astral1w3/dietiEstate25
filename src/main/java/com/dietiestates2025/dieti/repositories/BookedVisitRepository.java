package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.BookedVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedVisitRepository extends JpaRepository<BookedVisit, Integer> {

    // Trova tutte le visite prenotate per un determinato immobile
    List<BookedVisit> findByPropertyIdProperty(Integer propertyId);

    /**
     * Trova tutte le visite prenotate per una lista di ID di proprietà.
     * @param propertyIds La lista degli ID delle proprietà dell'agente.
     * @return Una lista di visite prenotate.
     */
    List<BookedVisit> findByPropertyIdPropertyIn(List<Integer> propertyIds);


    /**
     * Conta il numero di visite prenotate per un dato ID di proprietà.
     * @param propertyId L'ID della proprietà.
     * @return Il numero di visite.
     */
    long countByPropertyIdProperty(Integer propertyId);
}