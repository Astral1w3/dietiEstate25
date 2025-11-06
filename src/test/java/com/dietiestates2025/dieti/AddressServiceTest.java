package com.dietiestates2025.dieti;

import com.dietiestates2025.dieti.Service.AddressService;
import com.dietiestates2025.dieti.Service.MunicipalityService;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Province; // <-- Potrebbe essere necessario importare Province
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
        // GIVEN: Un comune "gestito" e un indirizzo di input
        
        // --- CORREZIONE 1: Crea un oggetto Province ---
        
        Province province = Province.builder().acronym("NA").provinceName("Napoli").build();
        
        managedMunicipality = Municipality.builder()
                .municipalityName("Napoli")
                .province(province) // Passa l'oggetto Province, non una String
                .build();

        inputAddress = Address.builder()
                .street("Via Roma")
                .houseNumber(10) // --- CORREZIONE 2: Passa un Integer, non una String ---
                .municipality(managedMunicipality)
                .build();
    }

    @Test
    void checkIfAddressExist_WhenAddressAlreadyExists_ShouldReturnExistingAddressAndNotSave() {
        // GIVEN: L'indirizzo di input esiste già nel database
        Address existingAddressInDb = Address.builder()
                .street("Via Roma")
                .houseNumber(10) // Anche qui, usa Integer
                .municipality(managedMunicipality)
                .build();
        
        // --- CORREZIONE 3: Imposta l'ID usando il setter dopo la creazione ---
        existingAddressInDb.setIdAddress(1); 

        // Configura i mock
        when(municipalityService.findOrCreateMunicipality(managedMunicipality)).thenReturn(managedMunicipality);
        when(addressRepository.findByStreetAndHouseNumberAndMunicipality(
                inputAddress.getStreet(),
                inputAddress.getHouseNumber(),
                managedMunicipality
        )).thenReturn(Optional.of(existingAddressInDb));

        // WHEN
        Address result = addressService.checkIfAddressExist(inputAddress);

        // THEN
        assertEquals(existingAddressInDb, result);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void checkIfAddressExist_WhenAddressDoesNotExist_ShouldSaveAndReturnNewAddress() {
        // GIVEN: L'indirizzo di input NON esiste nel database
        Address addressToSave = Address.builder()
                .street("Via Roma")
                .houseNumber(10) // Anche qui, usa Integer
                .municipality(managedMunicipality)
                .build();
        
        // --- CORREZIONE 4: Imposta l'ID sull'oggetto che SIMULA il ritorno dal DB ---
        addressToSave.setIdAddress(2); 

        // Configura i mock
        when(municipalityService.findOrCreateMunicipality(managedMunicipality)).thenReturn(managedMunicipality);
        when(addressRepository.findByStreetAndHouseNumberAndMunicipality(
                inputAddress.getStreet(),
                inputAddress.getHouseNumber(),
                managedMunicipality
        )).thenReturn(Optional.empty());
        
        // Simula il salvataggio restituendo l'oggetto con l'ID impostato
        when(addressRepository.save(inputAddress)).thenReturn(addressToSave);
        
        // WHEN
        Address result = addressService.checkIfAddressExist(inputAddress);

        // THEN
        assertEquals(addressToSave, result);
        verify(addressRepository, times(1)).save(inputAddress);
    }
}