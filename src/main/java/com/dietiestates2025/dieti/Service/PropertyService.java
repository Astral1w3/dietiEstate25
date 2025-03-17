package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.dto.AddressDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;

import jakarta.transaction.Transactional;

@Service
public class PropertyService {

    private final PropertyRepository repo;
    private final AddressService addressService;  // Iniezione di AddressService
    DozerBeanMapper mapper;

    public PropertyService(PropertyRepository repo, AddressService addressService, DozerBeanMapper mapper) {
        this.repo = repo;
        this.addressService = addressService;
        this.mapper = mapper;
    }

    @Transactional
    public PropertyDTO addProperty(PropertyDTO propertyDTO){ 
        Address address = mapper.map(propertyDTO.getAddress(),Address.class);
        Property property = mapper.map(propertyDTO,Property.class);
        property.setAddress(addressService.checkIfAddressExist(address));
        Property savedProperty = repo.save(property);
        PropertyDTO savedPropertyDTO = mapper.map(savedProperty, PropertyDTO.class);
        return savedPropertyDTO;
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
        PropertyDTO propertyDTO = mapper.map(property,PropertyDTO.class);
        return propertyDTO;
    }
}
