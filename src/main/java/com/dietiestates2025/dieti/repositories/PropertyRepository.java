package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dietiestates2025.dieti.model.Property;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

    @Query("SELECT p.price, a.street FROM Property p JOIN p.address a")
    List<Object[]> findAllPriceAndStreet();

    @Query("SELECT s.serviceName FROM Property p JOIN p.services s WHERE p.idProperty = :id")
    List<String> findAllServiceOfProperty(@Param("id") int id);
    
    List<Property> findByAddressMunicipalityMunicipalityNameIgnoreCase(String municipalityName);

        @Query("SELECT p FROM Property p JOIN p.address a JOIN a.municipality m WHERE lower(m.municipalityName) = lower(:location)")
    List<Property> findByLocationIgnoreCase(@Param("location") String location);

    @Query("SELECT p FROM Property p " +
           "JOIN p.address a " +
           "JOIN a.municipality m " +
           "JOIN m.province prov " +
           "WHERE p.propertyState.id = :stateId " +
           "AND (" +
           "   LOWER(a.street) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "   LOWER(m.municipalityName) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "   LOWER(prov.provinceName) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "   LOWER(prov.acronym) LIKE LOWER(CONCAT('%', :location, '%'))" +
           ")")
    List<Property> findByLocationAndState(
        @Param("location") String location, 
        @Param("stateId") Integer stateId
    );
}