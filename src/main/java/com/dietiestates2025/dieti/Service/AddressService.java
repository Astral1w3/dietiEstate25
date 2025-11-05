// in package com.dietiestates2025.dieti.Service;
package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Usa Lombok per un costruttore pulito
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final MunicipalityService municipalityService;

    // Il costruttore non è più necessario grazie a @RequiredArgsConstructor

    public void addAddressToDatabase(Address address) {
        addressRepository.save(address);
    }

    @Transactional
    public Address checkIfAddressExist(Address address) {
        // 1. Delega al MunicipalityService la logica complessa di ricerca/creazione.
        // Questo metodo ora restituisce un'entità Municipality valida e persistente.
        Municipality managedMunicipality = municipalityService.findOrCreateMunicipality(address.getMunicipality());
        
        // 2. Associa il comune corretto all'indirizzo prima di salvarlo.
        address.setMunicipality(managedMunicipality);

        // 3. Cerca l'indirizzo nel DB. Se non esiste, salvalo.
        // Questa operazione è ora sicura.
        return addressRepository.findByStreetAndHouseNumberAndMunicipality(
            address.getStreet(), 
            address.getHouseNumber(), 
            managedMunicipality
        ).orElseGet(() -> addressRepository.save(address));
    }

    public Optional<Address> findAddressbyId(Integer id){
        return addressRepository.findById(id);
    }
}