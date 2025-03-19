package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.dto.MunicipalityDTO;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;

@Service
public class MunicipalityService {

    MunicipalityRepository municipalityRepository;
    DozerBeanMapper mapper;


    public MunicipalityService(MunicipalityRepository municipalityRepository, DozerBeanMapper mapper ){ 
        this.municipalityRepository = municipalityRepository;
        this.mapper = mapper;
    }

    public MunicipalityDTO getMunicipalityDTOById(String zipcode) {
        Optional<Municipality> municipality =  municipalityRepository.findById(zipcode);
        MunicipalityDTO municipalityDTO = mapper.map(municipality.get(),MunicipalityDTO.class);
        return municipalityDTO;
    }

    public Municipality getMunicipalityById(String zipcode) {
        return municipalityRepository.findById(zipcode).get();
    }
    

}
