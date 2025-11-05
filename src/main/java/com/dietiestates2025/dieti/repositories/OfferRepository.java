package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dietiestates2025.dieti.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Integer> {
    /**
     * Trova tutte le offerte per una lista di ID di proprietà.
     * @param propertyIds La lista degli ID delle proprietà dell'agente.
     * @return Una lista di offerte.
     */
    List<Offer> findByPropertyIdPropertyIn(List<Integer> propertyIds);

    List<Offer> findByPropertyIdProperty(Integer propertyId);

    // --- METODO CORRETTO COME HAI SUGGERITO ---
    /**
     * Conta il numero di offerte ricevute per un dato ID di proprietà.
     * @param propertyId L'ID della proprietà.
     * @return Il numero di offerte.
     */
    long countByPropertyIdProperty(Integer propertyId);


    @Query("SELECT COUNT(o) FROM Offer o WHERE o.property.idProperty IN :propertyIds")
    long countByPropertyIds(@Param("propertyIds") List<Integer> propertyIds);
    
}