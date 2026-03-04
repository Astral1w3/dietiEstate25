package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.Region;

public interface RegionRepository extends JpaRepository<Region, Integer>{


    @Query("SELECT r.regionName, p.provinceName FROM Region r JOIN r.provinces p")
    List<Object[]> findAllRegionWithProvinces();
    
}
