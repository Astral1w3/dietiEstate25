package com.dietiestates2025.dieti.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.PropertyRepository;

@Service
public class PropertyService {

    PropertyRepository repo;

    @Autowired
    public void PropertyService(PropertyRepository repo){
        this.repo = repo;

    }

    public Property getPropertyById(int propertyId) {
        return repo.findById(propertyId).orElse(new Property());
    }
    
}
