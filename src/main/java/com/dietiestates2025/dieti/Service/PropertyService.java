package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;

import jakarta.transaction.Transactional;

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


    @Autowired
    private AddressRepository addressRepository;

    @Transactional
    public Property addProperty(Property property){ 
        if (property.getAddress() != null) {
            Address address = property.getAddress();
            
            // 🔹 Controlla se l'indirizzo esiste già nel database
            if (address.getIdAddress() != null) {
                Optional<Address> existingAddress = addressRepository.findById(address.getIdAddress());
                if (existingAddress.isPresent()) {
                    address = existingAddress.get(); // Usa l'indirizzo già esistente
                } else {
                    address.setIdAddress(null); // 🔹 Imposta ID a null per evitare duplicati
                    address = addressRepository.save(address); // Salva solo se nuovo
                }
            } else {
                address = addressRepository.save(address); // Salva l'indirizzo se non ha un ID
            }

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

    
    

    
}
