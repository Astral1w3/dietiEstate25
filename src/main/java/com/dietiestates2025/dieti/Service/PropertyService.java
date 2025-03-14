package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public PropertyService(PropertyRepository repo, AddressService addressService) {
        this.repo = repo;
        this.addressService = addressService;
    }

    @Transactional
    public Property addProperty(Property property) {
        if (property.getAddress() != null) {
            Address address = property.getAddress();
            
            addressService.checkIfAddressExist(address);

            property.setAddress(address);
        }

        return repo.save(property);
    }

    public boolean deleteProperty(int propertyId) {
        if(repo.existsById(propertyId)){
            repo.deleteById(propertyId);
            return true;
        }
        return false;
    }
    
    public Property getPropertyById(int propertyId) {
        return repo.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
    }
}
