package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Province;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;
import com.dietiestates2025.dieti.repositories.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MunicipalityService {

    private final MunicipalityRepository municipalityRepository;
    private final ProvinceRepository provinceRepository;

    /**
     * Cerca un comune tramite il suo CAP (chiave primaria). Se esiste, lo restituisce.
     * Altrimenti, ne crea uno nuovo dopo aver validato e associato la provincia corrispondente.
     *
     * @param municipalityFromDto L'oggetto {@link Municipality} proveniente da un DTO o da un'altra fonte,
     *                            potenzialmente non ancora gestito dal contesto di persistenza.
     * @return L'entità {@link Municipality} gestita, recuperata dal database o appena creata.
     * @throws IllegalArgumentException se i dati del comune o della provincia sono incompleti.
     * @throws ResourceNotFoundException se la provincia associata al nuovo comune non esiste nel database.
     */
    @Transactional
    public Municipality findOrCreateMunicipality(Municipality municipalityFromDto) {
        // Validazione preliminare per assicurarsi che i dati essenziali non siano null.
        if (municipalityFromDto == null || municipalityFromDto.getZipCode() == null) {
            throw new IllegalArgumentException("Dati del comune (zipCode) incompleti.");
        }
        // 1. Controlla se il comune esiste già tramite la sua chiave primaria (zipCode).
        Optional<Municipality> existingMunicipality = municipalityRepository.findById(municipalityFromDto.getZipCode());
        if (existingMunicipality.isPresent()) {
            return existingMunicipality.get();
        }
        // 2. Se il comune non esiste, procediamo con la creazione.
        // È necessario prima trovare o validare la sua provincia.
        Province provinceFromDto = municipalityFromDto.getProvince();
        if (provinceFromDto == null || provinceFromDto.getProvinceName() == null || provinceFromDto.getProvinceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome della provincia mancante per la creazione del comune.");
        }
        // 3. Cerca la provincia nel database usando il nome (ignorando maiuscole/minuscole).
        // Se la provincia non esiste, lancia un'eccezione chiara perché non possiamo creare un comune senza di essa.
        Province managedProvince = provinceRepository.findByProvinceNameIgnoreCase(provinceFromDto.getProvinceName())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Provincia non trovata con nome: '" + provinceFromDto.getProvinceName() + "'. Impossibile creare il comune."
                ));

        // 4. Associa l'entità Provincia "gestita" (cioè quella recuperata dal DB) al nuovo comune.
        // Questo è un passaggio cruciale per stabilire correttamente la relazione JPA.
        municipalityFromDto.setProvince(managedProvince);
        
        // 5. Salva il nuovo comune nel database. L'operazione di salvataggio è parte della transazione.
        return municipalityRepository.save(municipalityFromDto);
    }

    public Municipality getMunicipalityById(String zipCode) {
        return municipalityRepository.findById(zipCode)
            .orElseThrow(() -> new ResourceNotFoundException("Comune non trovato con CAP: " + zipCode));
    }
}