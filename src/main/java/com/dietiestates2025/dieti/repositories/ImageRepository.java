package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dietiestates2025.dieti.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query("SELECT i.image FROM Image i JOIN i.property p WHERE p.idProperty = :idProperty")
    List<Image> findByPropertyId(@Param("idProperty") Long idProperty);


}
