package com.dietiestates2025.dieti.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.PropertyRepository;

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
        Optional<Property> optProperty = repo.findById(propertyId);
        Property property = optProperty.get();
        PropertyDTO propertyDTO = dozerBeanMapper.map(property,PropertyDTO.class);
        return propertyDTO;
    }

     public List<PropertyDTO> findPropertiesByLocation(String location) {
        List<Property> properties = repo.findByAddressMunicipalityMunicipalityNameIgnoreCase(location);
        return properties.stream()
                         .map(property -> dozerBeanMapper.map(property, PropertyDTO.class))
                         .collect(Collectors.toList());
    }
}
