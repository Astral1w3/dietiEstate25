package com.dietiestates2025.dieti.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates2025.dieti.Service.MunicipalityService;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.Service.RegionService;
import com.dietiestates2025.dieti.dto.MunicipalityDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.dto.RegionDTO;


@RestController
public class PropertyController {
    
    PropertyService propertyService;
    RegionService regionService;
    MunicipalityService municipalityService;

    public PropertyController(PropertyService service, RegionService regionService, MunicipalityService municipalityService){
        this.propertyService = service;
        this.regionService = regionService;
        this.municipalityService = municipalityService;
    }

    @PostMapping("/add")
    public ResponseEntity<PropertyDTO> addProperty(@RequestBody PropertyDTO propertyDTO) {
        PropertyDTO savedPropertyDTO = propertyService.addProperty(propertyDTO); 
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);
    }


    @DeleteMapping("/delete/{propertyId}")
    public ResponseEntity<String> deleteProperty(@PathVariable int propertyId) {
        boolean deleted = propertyService.deleteProperty(propertyId);
        if (deleted) {
            return ResponseEntity.ok("Property eliminata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property non trovata.");
        }
    }

    @GetMapping("/properties/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable int propertyId) {
        PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(propertyDTO);
    }

    @GetMapping("/test/{regionId}")
    public ResponseEntity<RegionDTO> getAddressByRegionId(@PathVariable int regionId) {
        RegionDTO regionDTO = regionService.getRegionById(regionId);
        return ResponseEntity.ok(regionDTO);
    }

    @GetMapping("/test1/{zipcode}")
    public ResponseEntity<MunicipalityDTO> getAddressByRegionId(@PathVariable String zipcode) {
        MunicipalityDTO municipalityDTO = municipalityService.getMunicipalityDTOById(zipcode);
        return ResponseEntity.ok(municipalityDTO);
    }
    
    @GetMapping("/properties/search")
    public ResponseEntity<List<PropertyDTO>> searchPropertiesByLocation(@RequestParam String location) {
        List<PropertyDTO> properties = propertyService.findPropertiesByLocation(location);
        return ResponseEntity.ok(properties);
    }


}
