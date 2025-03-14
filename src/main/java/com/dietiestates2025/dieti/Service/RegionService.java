package com.dietiestates2025.dieti.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Region;
import com.dietiestates2025.dieti.repositories.RegionRepository;

@Service
public class RegionService {

    RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository){ 
        this.regionRepository = regionRepository;
    }

    public Region getRegionById(int regionId) {
        return regionRepository.findById(regionId)
            .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + regionId));
    }

    
}
