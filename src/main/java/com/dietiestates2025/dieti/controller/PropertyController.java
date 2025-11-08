package com.dietiestates2025.dieti.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.dto.ApiResponse;
import com.dietiestates2025.dieti.dto.PagedResponseDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.dto.PropertyStateUpdateDTO;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    public PropertyController(PropertyService propertyService, ObjectMapper objectMapper){
        this.propertyService = propertyService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/search", params = "location") 
    public ResponseEntity<PagedResponseDTO<PropertyDTO>> findPropertiesByLocationAvailable(
        @RequestParam String location,
        Pageable pageable
    ) {
        PagedResponseDTO<PropertyDTO> response = propertyService.findPropertiesByLocationAvailable(location, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable int propertyId) {
        PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(propertyDTO);
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> addProperty(
        @RequestPart("propertyData") String propertyDataJson, 
        @RequestPart("images") List<MultipartFile> images,
        Authentication authentication
    ) throws JsonProcessingException {
        

        PropertyDTO propertyDTO = objectMapper.readValue(propertyDataJson, PropertyDTO.class);
        String userEmail = authentication.getName();
        
        PropertyDTO savedPropertyDTO = propertyService.addPropertyAndImages(propertyDTO, images, userEmail); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse> deleteProperty(@PathVariable int propertyId) {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.ok(new ApiResponse(true, "Proprietà eliminata con successo."));
    }
  
    @PostMapping("/{propertyId}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer propertyId) {
        propertyService.incrementViewCount(propertyId);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{propertyId}/state")
    public ResponseEntity<Void> updatePropertyState(
        @PathVariable int propertyId, 
        @Valid @RequestBody PropertyStateUpdateDTO stateUpdateDTO
    ) {
        propertyService.updatePropertyState(propertyId, stateUpdateDTO.getState());
        return ResponseEntity.ok().build();
    }

}