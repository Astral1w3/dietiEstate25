package com.dietiestates2025.dieti.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException; // Assicurati di importare la tua eccezione

import com.dietiestates2025.dieti.Service.MunicipalityService;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.Service.RegionService;
import com.dietiestates2025.dieti.dto.PropertyDTO;


@RestController
@RequestMapping("/api")
public class PropertyController {
    
    PropertyService propertyService;
    RegionService regionService;
    MunicipalityService municipalityService;

    public PropertyController(PropertyService service, RegionService regionService, MunicipalityService municipalityService){
        this.propertyService = service;
        this.regionService = regionService;
        this.municipalityService = municipalityService;
    }

    @PostMapping("/properties")
    public ResponseEntity<PropertyDTO> addProperty(@RequestBody PropertyDTO propertyDTO) {
        PropertyDTO savedPropertyDTO = propertyService.addProperty(propertyDTO); 
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);
    }


    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<String> deleteProperty(@PathVariable int propertyId) {
        boolean deleted = propertyService.deleteProperty(propertyId);
        if (deleted) {
            return ResponseEntity.ok("Property eliminata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property non trovata.");
        }
    }

    // Nel tuo PropertyController.java

    @GetMapping("/properties/{propertyId}")
    public ResponseEntity<?> getPropertyById(@PathVariable int propertyId) {
        try {
            // Prova a recuperare la proprietà come prima
            PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
            
            // Se tutto va bene, restituisci 200 OK con i dati della proprietà
            return ResponseEntity.ok(propertyDTO);

        } catch (ResourceNotFoundException e) {
            // Se il service lancia l'eccezione, catturala qui!
            // Restituisci una risposta 404 Not Found con il messaggio di errore dal service.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    
    @GetMapping("/properties/search")
    public ResponseEntity<List<PropertyDTO>> searchPropertiesByLocation(@RequestParam String location) {
        List<PropertyDTO> properties = propertyService.findPropertiesByLocation(location);
        return ResponseEntity.ok(properties);
    }


}
