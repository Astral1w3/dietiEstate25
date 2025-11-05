package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.BookedVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;         // <-- IMPORT AGGIUNTO
import org.springframework.data.repository.query.Param;    // <-- IMPORT AGGIUNTO
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
    

    // --- NUOVO METODO AGGIUNTO PER IL CONTEGGIO EFFICIENTE ---
    /**
     * Conta il numero totale di visite per una lista di ID di proprietà.
     * @param propertyIds La lista degli ID.
     * @return Il conteggio totale.
     */
    @Query("SELECT COUNT(b) FROM BookedVisit b WHERE b.property.idProperty IN :propertyIds")
    long countByPropertyIds(@Param("propertyIds") List<Integer> propertyIds);
}