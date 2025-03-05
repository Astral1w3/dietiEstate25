package com.dietiestates2025.dieti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates2025.dieti.DTO.PropertyDTO;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.mapper.PropertyMapper;
import com.dietiestates2025.dieti.model.Property;

@RestController
public class PropertyController {
    

    PropertyService service;
    @Autowired
    public PropertyController(PropertyService service){
        this.service = service;
    }


    @GetMapping("/get/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable int propertyId){
        Property property = service.getPropertyById(propertyId);
        if (property == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PropertyMapper.toDTO(property));
    }
}
