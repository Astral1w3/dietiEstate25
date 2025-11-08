package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor 
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final MunicipalityService municipalityService;

    public void addAddressToDatabase(Address address) {
        addressRepository.save(address);
    }
     /**
     * Controlla se un indirizzo esiste già nel database; in caso contrario, lo crea.
     * Questo metodo implementa il pattern "find or create".
     * L'operazione è transazionale: se una qualsiasi parte del processo fallisce,
     * tutte le modifiche al database vengono annullate (rollback).
     * 
     * 1. Trova o crea il comune (Municipality) associato all'indirizzo.
     * 2. Aggiorna l'indirizzo con l'entità Municipality gestita dal persistence context.
     * 3. Cerca un indirizzo esistente con la stessa via, numero civico e comune.
     * 4. Se lo trova, restituisce l'indirizzo esistente.
     * 5. Altrimenti, salva il nuovo indirizzo e lo restituisce.
     *
     * @param address L'oggetto Address da cercare o creare.
     * @return L'entità Address corrispondente, recuperata dal database o appena creata.
     */
    @Transactional
    public Address checkIfAddressExist(Address address) {
        Municipality managedMunicipality = municipalityService.findOrCreateMunicipality(address.getMunicipality());
        
        address.setMunicipality(managedMunicipality);

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