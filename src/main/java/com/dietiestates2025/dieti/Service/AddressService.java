package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.repositories.AddressRepository;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;


    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void addAddressToDatabase(Address address) {
        addressRepository.save(address);
    }

    public void checkIfAddressExist(Address address) {
        Optional<Address> result = addressRepository.findByStreetAndHouseNumberAndMunicipality(
            address.getStreet(), address.getHouseNumber(), address.getMunicipality());
        
        if (result.isPresent()) return;
        else addAddressToDatabase(address);
        
    }
}
