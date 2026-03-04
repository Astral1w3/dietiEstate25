package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, String> {
    
    Optional<Province> findByProvinceNameIgnoreCase(String provinceName);

}