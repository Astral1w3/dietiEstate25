package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.Municipality;

public interface MunicipalityRepository extends JpaRepository<Municipality, String> {

    @Query("SELECT m.municipalityName FROM Municipality m WHERE m.province.provinceName = 'Torino'")
    List<Object[]> findAllProvincesOfTorino();


}