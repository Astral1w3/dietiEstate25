package com.dietiestates2025.dieti.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service // <-- ANNOTAZIONE FONDAMENTALE: Dice a Spring di gestire questa classe
public class FileStorageService {

    private final Path fileStorageLocation;

    // Il costruttore legge il percorso dal file application.yml
    // ***** QUESTA È LA RIGA CORRETTA *****
    public FileStorageService(@Value("${spring.servlet.multipart.location}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Crea la cartella di upload se non esiste
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Non è stato possibile creare la cartella dove salvare i file.", ex);
        }
    }

    /**
     * Salva un file su disco.
     * @param file il file ricevuto dalla richiesta HTTP
     * @return il nome univoco generato per il file salvato
     */
    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Controlli di sicurezza base sul nome del file
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Il nome del file contiene una sequenza non valida: " + originalFileName);
            }

            // Crea un nome di file univoco per evitare che due file con lo stesso nome si sovrascrivano
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Costruisce il percorso completo e salva il file
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossibile salvare il file " + originalFileName, ex);
        }
    }

    /**
     * Carica un file dal disco come risorsa.
     * @param fileName il nome del file da caricare
     * @return la risorsa da inviare nella risposta HTTP
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File non trovato: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File non trovato: " + fileName, ex);
        }
    }
}