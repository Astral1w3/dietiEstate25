package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;         // <-- IMPORT AGGIUNTO
import org.springframework.data.domain.Pageable;       // <-- IMPORT AGGIUNTO
import com.dietiestates2025.dieti.model.Property;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

    @Query("SELECT p.price, a.street FROM Property p JOIN p.address a")
    List<Object[]> findAllPriceAndStreet();

    @Query("SELECT s.serviceName FROM Property p JOIN p.services s WHERE p.idProperty = :id")
    List<String> findAllServiceOfProperty(@Param("id") int id);
    
    List<Property> findByAddressMunicipalityMunicipalityNameIgnoreCase(String municipalityName);

        @Query("SELECT p FROM Property p JOIN p.address a JOIN a.municipality m WHERE lower(m.municipalityName) = lower(:location)")
    List<Property> findByLocationIgnoreCase(@Param("location") String location);


    // Query 1: Modificata per una ricerca più specifica.
    @Query(value = "SELECT p.idProperty FROM Property p " +
                   "JOIN p.address a " +
                   "JOIN a.municipality m " +
                   "JOIN m.province prov " +
                   "JOIN prov.region r " + // <-- JOIN ALLA REGIONE AGGIUNTO
                   "WHERE p.propertyState.id = :stateId AND (" +
                   // "   LOWER(a.street) LIKE CONCAT('%', :location, '%') OR " + // <-- RIGA RIMOSSA
                   "   LOWER(m.municipalityName) LIKE CONCAT('%', :location, '%') OR " +
                   "   LOWER(prov.provinceName) LIKE CONCAT('%', :location, '%') OR " +
                   "   LOWER(r.regionName) LIKE CONCAT('%', :location, '%') OR " + // <-- RIGA AGGIUNTA
                   "   LOWER(prov.acronym) LIKE CONCAT('%', :location, '%')" +
                   ")",
            countQuery = "SELECT COUNT(p.idProperty) FROM Property p " +
                         "JOIN p.address a " +
                         "JOIN a.municipality m " +
                         "JOIN m.province prov " +
                         "JOIN prov.region r " + // <-- JOIN ALLA REGIONE AGGIUNTO ANCHE QUI
                         "WHERE p.propertyState.id = :stateId AND (" +
                         // "LOWER(a.street) LIKE CONCAT('%', :location, '%') OR " + // <-- RIGA RIMOSSA
                         "LOWER(m.municipalityName) LIKE CONCAT('%', :location, '%') OR " +
                         "LOWER(prov.provinceName) LIKE CONCAT('%', :location, '%') OR " +
                         "LOWER(r.regionName) LIKE CONCAT('%', :location, '%') OR " + // <-- RIGA AGGIUNTA
                         "LOWER(prov.acronym) LIKE CONCAT('%', :location, '%'))")
    Page<Integer> findIdsByLocationAndState(
            @Param("location") String location,
            @Param("stateId") Integer stateId,
            Pageable pageable
    );

    // Query 2: Carica tutti i dettagli per una lista specifica di ID.
    @Query("SELECT DISTINCT p FROM Property p " +
           "LEFT JOIN FETCH p.address a " +
           "LEFT JOIN FETCH a.municipality m " +
           "LEFT JOIN FETCH m.province prov " +
           "LEFT JOIN FETCH prov.region r " +
           "LEFT JOIN FETCH p.propertyState ps " +
           "LEFT JOIN FETCH p.images i " +
           "LEFT JOIN FETCH p.saleTypes st " +
           "LEFT JOIN FETCH p.services s " +
           "LEFT JOIN FETCH p.propertyStats p_stats " +
           "LEFT JOIN FETCH p.dashboards d " +
           "WHERE p.idProperty IN :ids")
    List<Property> findFullPropertiesByIds(@Param("ids") List<Integer> ids);

    /**
     * Aggiorna lo stato di una singola proprietà in modo efficiente
     * eseguendo una query UPDATE diretta.
     * @param propertyId L'ID della proprietà da aggiornare.
     * @param newStateId L'ID del nuovo stato da impostare.
     */
    @Modifying // Indica che questa query modifica i dati (non è una SELECT)
    @Query("UPDATE Property p SET p.propertyState.id = :newStateId WHERE p.id = :propertyId")
    void updatePropertyState(@Param("propertyId") int propertyId, @Param("newStateId") int newStateId);
    


}