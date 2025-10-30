package com.dietiestates2025.dieti.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.exception.*;

import jakarta.transaction.Transactional;

@Service
public class PropertyService {

    private final PropertyRepository repo;
    private final AddressService addressService;  // Iniezione di AddressService
    private final DozerBeanMapper dozerBeanMapper;

    public PropertyService(PropertyRepository repo, AddressService addressService, DozerBeanMapper dozerBeanMapper) {
        this.repo = repo;
        this.addressService = addressService;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    @Transactional
    public PropertyDTO addProperty(PropertyDTO propertyDTO){ 
        Address address = dozerBeanMapper.map(propertyDTO.getAddress(),Address.class);
        Property property = dozerBeanMapper.map(propertyDTO,Property.class);

        property.setAddress(addressService.checkIfAddressExist(address));
        Property savedProperty = repo.save(property);

        return dozerBeanMapper.map(savedProperty, PropertyDTO.class);
    }

    public boolean deleteProperty(int propertyId) {
        if(repo.existsById(propertyId)){
            repo.deleteById(propertyId);
            return true;
        }
        return false;
    }
    public PropertyDTO getPropertyById(int propertyId) {
        Property property = repo.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        return mapToDtoWithImageUrls(property); // Usa il nuovo metodo
    }

    public List<PropertyDTO> findPropertiesByLocation(String location) {
        List<Property> properties = repo.findByLocationIgnoreCase(location); // o il nome del tuo metodo
        return properties.stream()
                         .map(this::mapToDtoWithImageUrls) // Usa il nuovo metodo
                         .collect(Collectors.toList());
    }


    
    private PropertyDTO mapToDtoWithImageUrls(Property property) {
        PropertyDTO dto = dozerBeanMapper.map(property, PropertyDTO.class);
        
        // Per ogni entità Image, costruisci l'URL completo
        List<String> imageUrls = property.getImages().stream()
            .map(image -> ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(image.getFileName())
                    .toUriString())
            .collect(Collectors.toList());
            
        dto.setImageUrls(imageUrls);
        return dto;
    }
}
