package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dietiestates2025.dieti.model.Image;

// File: com/dietiestates2025/dieti/repositories/ImageRepository.java

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPropertyIdProperty(Long idProperty);

}
