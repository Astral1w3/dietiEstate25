package com.dietiestates2025.dieti;

import com.dietiestates2025.dieti.Service.AddressService;
import com.dietiestates2025.dieti.Service.MunicipalityService;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Province;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MunicipalityService municipalityService;

    @InjectMocks
    private AddressService addressService;

    private Municipality managedMunicipality;
    private Address inputAddress;

    @BeforeEach
    void setUp() {
        
        Province province = Province.builder().acronym("NA").provinceName("Napoli").build();
        
        managedMunicipality = Municipality.builder()
                .municipalityName("Napoli")
                .province(province)
                .build();

        inputAddress = Address.builder()
                .street("Via Roma")
                .houseNumber(10) 
                .municipality(managedMunicipality)
                .build();
    }

    @Test
    void checkIfAddressExist_WhenAddressAlreadyExists_ShouldReturnExistingAddressAndNotSave() {
        Address existingAddressInDb = Address.builder()
                .street("Via Roma")
                .houseNumber(10)
                .municipality(managedMunicipality)
                .build();
        
        existingAddressInDb.setIdAddress(1); 

        when(municipalityService.findOrCreateMunicipality(managedMunicipality)).thenReturn(managedMunicipality);
        when(addressRepository.findByStreetAndHouseNumberAndMunicipality(
                inputAddress.getStreet(),
                inputAddress.getHouseNumber(),
                managedMunicipality
        )).thenReturn(Optional.of(existingAddressInDb));

        Address result = addressService.checkIfAddressExist(inputAddress);

        assertEquals(existingAddressInDb, result);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void checkIfAddressExist_WhenAddressDoesNotExist_ShouldSaveAndReturnNewAddress() {
        Address addressToSave = Address.builder()
                .street("Via Roma")
                .houseNumber(10)
                .municipality(managedMunicipality)
                .build();
        
        addressToSave.setIdAddress(2); 

        when(municipalityService.findOrCreateMunicipality(managedMunicipality)).thenReturn(managedMunicipality);
        when(addressRepository.findByStreetAndHouseNumberAndMunicipality(
                inputAddress.getStreet(),
                inputAddress.getHouseNumber(),
                managedMunicipality
        )).thenReturn(Optional.empty());
        
        when(addressRepository.save(inputAddress)).thenReturn(addressToSave);
        
        Address result = addressService.checkIfAddressExist(inputAddress);

        assertEquals(addressToSave, result);
        verify(addressRepository, times(1)).save(inputAddress);
    }
}