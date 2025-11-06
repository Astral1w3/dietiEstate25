package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.BookedVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;         // <-- IMPORT AGGIUNTO
import org.springframework.data.repository.query.Param;    // <-- IMPORT AGGIUNTO
import org.springframework.stereotype.Repository;

import java.util.Date; 
import java.util.List;

@Repository
public interface BookedVisitRepository extends JpaRepository<BookedVisit, Integer> {

    interface PropertyCount {
        Integer getPropertyId();
        Long getCount();
    }

    // Nuovo metodo per OTTIMIZZAZIONE 1
    @Query("SELECT bv.visitDate FROM BookedVisit bv WHERE bv.property.idProperty = :propertyId")
    List<Date> findVisitDatesByPropertyId(@Param("propertyId") Integer propertyId);

    // Nuovo metodo per il controllo di ROBUSTEZZA
    boolean existsByPropertyIdPropertyAndVisitDate(Integer propertyId, Date visitDate);

    // Metodo modificato per OTTIMIZZAZIONE 2 (N+1 Query)
    @Query("SELECT bv FROM BookedVisit bv " +
           "JOIN FETCH bv.user " +
           "JOIN FETCH bv.property p " +
           "JOIN FETCH p.address a " +
           "JOIN FETCH a.municipality " +
           "WHERE p.idProperty IN :propertyIds")
    List<BookedVisit> findWithDetailsByPropertyIds(@Param("propertyIds") List<Integer> propertyIds);


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

    @Query("SELECT bv.property.idProperty as propertyId, COUNT(bv.id) as count " +
           "FROM BookedVisit bv " +
           "WHERE bv.property.idProperty IN :propertyIds " +
           "GROUP BY bv.property.idProperty")
    List<PropertyCount> countVisitsByPropertyIdsGrouped(@Param("propertyIds") List<Integer> propertyIds);



    // Il vecchio metodo può essere rimosso o tenuto se usato altrove
    List<BookedVisit> findByPropertyIdProperty(Integer propertyId);
    List<BookedVisit> findByPropertyIdPropertyIn(List<Integer> propertyIds);
}