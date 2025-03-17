package com.dietiestates2025.dieti.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.repositories.AddressRepository;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final MunicipalityService municipalityService;


    public AddressService(AddressRepository addressRepository, MunicipalityService municipalityService) {
        this.addressRepository = addressRepository;
        this.municipalityService = municipalityService;
    }

    public void addAddressToDatabase(Address address) {
        addressRepository.save(address);
    }

    public Address checkIfAddressExist(Address address) {
        Address a = addressRepository.findByStreetAndHouseNumberAndMunicipality(
            address.getStreet(), address.getHouseNumber(), address.getMunicipality()).orElseGet(()-> addressRepository.save(address)); 
        a.setMunicipality(municipalityService.getMunicipalityById(a.getMunicipality().getZipCode()));
        return a;
    }

    public Optional<Address> findAddressbyId(Integer id){
        return addressRepository.findById(id);
    }
}
