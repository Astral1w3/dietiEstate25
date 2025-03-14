package com.dietiestates2025.dieti.Service;

import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Region;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;
import com.dietiestates2025.dieti.repositories.RegionRepository;

@Service
public class MunicipalityService {

    MunicipalityRepository municipalityRepository;

    public MunicipalityService(MunicipalityRepository municipalityRepository){ 
        this.municipalityRepository = municipalityRepository;
    }

    public Municipality getMunicipalityById(String zipcode) {
        return municipalityRepository.findById(zipcode)
            .orElseThrow(() -> new ResourceNotFoundException("Municipality not found with id: " + zipcode));
    }

}
