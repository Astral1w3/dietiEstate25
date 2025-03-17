package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.dto.RegionDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Region;
import com.dietiestates2025.dieti.repositories.RegionRepository;

@Service
public class RegionService {


    RegionRepository regionRepository;

    DozerBeanMapper mapper;

    public RegionService(RegionRepository regionRepository, DozerBeanMapper mapper){ 
        this.regionRepository = regionRepository;
        this.mapper = mapper;
    }

    public RegionDTO getRegionById(int regionId) {
        Optional<Region> res = regionRepository.findById(regionId);
        RegionDTO dto = mapper.map(res.get(), RegionDTO.class);
        return dto;

    }

    
}
