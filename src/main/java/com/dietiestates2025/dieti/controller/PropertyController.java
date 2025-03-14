package com.dietiestates2025.dieti.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates2025.dieti.Service.MunicipalityService;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.Service.RegionService;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.Region;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestParam;


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
    public ResponseEntity<Property> addProperty(@RequestBody Property property) {
        Property savedProperty = propertyService.addProperty(property); 
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProperty);
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
    public ResponseEntity<Property> getPropertyById(@PathVariable int propertyId) {
        Property p = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/test/{regionId}")
    public ResponseEntity<Region> getAddressByRegionId(@PathVariable int regionId) {
        Region r = regionService.getRegionById(regionId);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/test1/{zipcode}")
    public ResponseEntity<Municipality> getAddressByRegionId(@PathVariable String zipcode) {
        Municipality m = municipalityService.getMunicipalityById(zipcode);
        return ResponseEntity.ok(m);
    }
    


}
