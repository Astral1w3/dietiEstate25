package com.dietiestates2025.dieti.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates2025.dieti.DTO.AddressDTO;
import com.dietiestates2025.dieti.DTO.PropertyDTO;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.mapper.PropertyMapper;
import com.dietiestates2025.dieti.model.Property;

@RestController
public class PropertyController {
    

    PropertyService propertyService;
    @Autowired
    public PropertyController(PropertyService service){
        this.propertyService = service;
    }


    @GetMapping("/get/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable int propertyId){
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);

        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get(); // Estrae il valore da Optional
            return ResponseEntity.ok(PropertyMapper.toDTO(property)); // Ritorna 200 OK con il DTO
        } else {
            return ResponseEntity.notFound().build(); // Se non esiste, ritorna 404
        }
    }

    @PostMapping("/add")
    public ResponseEntity<PropertyDTO> addProperty(@RequestBody PropertyDTO propertyDTO) {



        Property property = PropertyMapper.toEntity(propertyDTO);
        Property savedProperty = propertyService.addProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(PropertyMapper.toDTO(savedProperty));
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

}
