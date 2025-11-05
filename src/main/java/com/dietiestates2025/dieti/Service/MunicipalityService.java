// in package com.dietiestates2025.dieti.Service;
package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.exception.ResourceNotFoundException; // Assicurati di avere l'import
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Province;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;
import com.dietiestates2025.dieti.repositories.ProvinceRepository; // <-- NUOVA DIPENDENZA
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MunicipalityService {

    private final MunicipalityRepository municipalityRepository;
    private final ProvinceRepository provinceRepository; // <-- INIETTA QUESTO

    @Transactional
    public Municipality findOrCreateMunicipality(Municipality municipalityFromDto) {
        if (municipalityFromDto == null || municipalityFromDto.getZipCode() == null) {
            throw new IllegalArgumentException("Dati del comune (zipCode) incompleti.");
        }

        // 1. Controlla se il comune esiste già per ID (zipCode). Se sì, abbiamo finito.
        Optional<Municipality> existingMunicipality = municipalityRepository.findById(municipalityFromDto.getZipCode());
        if (existingMunicipality.isPresent()) {
            return existingMunicipality.get();
        }

        // 2. Se il comune non esiste, dobbiamo crearlo. Prima, troviamo la sua provincia.
        Province provinceFromDto = municipalityFromDto.getProvince();
        if (provinceFromDto == null || provinceFromDto.getProvinceName() == null || provinceFromDto.getProvinceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome della provincia mancante per la creazione del comune.");
        }

        // 3. Cerca la provincia nel DB usando il nome fornito.
        Province managedProvince = provinceRepository.findByProvinceNameIgnoreCase(provinceFromDto.getProvinceName())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Provincia non trovata con nome: '" + provinceFromDto.getProvinceName() + "'. Impossibile creare il comune."
                ));

        // 4. Ora che abbiamo la provincia "gestita" (dal DB), la associamo al nuovo comune.
        municipalityFromDto.setProvince(managedProvince);

        // 5. Infine, salviamo il nuovo comune.
        return municipalityRepository.save(municipalityFromDto);
    }

    public Municipality getMunicipalityById(String zipCode) {
        return municipalityRepository.findById(zipCode)
            .orElseThrow(() -> new ResourceNotFoundException("Comune non trovato con CAP: " + zipCode));
    }
}