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

}