// in package com.dietiestates2025.dieti.repositories;
package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional; // Aggiungi questo import

public interface ProvinceRepository extends JpaRepository<Province, String> {
    
    // Nuovo metodo per trovare una provincia tramite il suo nome (case-insensitive)
    Optional<Province> findByProvinceNameIgnoreCase(String provinceName);

}