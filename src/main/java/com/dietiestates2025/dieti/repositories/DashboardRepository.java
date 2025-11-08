package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DashboardRepository extends JpaRepository<Dashboard, String> {

    Optional<Dashboard> findByUserEmail(String email);

    @Query("SELECT d FROM Dashboard d " +
           "LEFT JOIN FETCH d.properties p " +
           "LEFT JOIN FETCH p.address a " +
           "LEFT JOIN FETCH a.municipality m " +
           "LEFT JOIN FETCH p.propertyState ps " +
           "LEFT JOIN FETCH p.images i " +
           "LEFT JOIN FETCH p.saleTypes st " +
           "LEFT JOIN FETCH p.propertyStats p_stats " +
           "WHERE d.email = :email")
    Optional<Dashboard> findByEmailWithFullProperties(@Param("email") String email);


    @Query("SELECT p.idProperty FROM Dashboard d JOIN d.properties p WHERE d.email = :email")
    List<Integer> findPropertyIdsByEmail(@Param("email") String email);
}