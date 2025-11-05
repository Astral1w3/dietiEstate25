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
import java.util.Objects; // <-- IMPORT NECESSARIO
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${spring.servlet.multipart.location}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Non è stato possibile creare la cartella dove salvare i file.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // --- INIZIO BLOCCO CORRETTO ---
        
        // 1. Estrai il nome del file originale
        String originalFileName = file.getOriginalFilename();

        // 2. Controlla che il nome non sia nullo o vuoto prima di usarlo.
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new RuntimeException("Impossibile salvare il file: il nome del file non è valido.");
        }

        // 3. Ora che sappiamo che non è nullo, possiamo pulirlo in sicurezza.
        String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(originalFileName));
        
        // --- FINE BLOCCO CORRETTO ---

        try {
            // Controlli di sicurezza base sul nome del file pulito
            if (cleanFileName.contains("..")) {
                throw new RuntimeException("Il nome del file contiene una sequenza non valida: " + cleanFileName);
            }

            // Crea un nome di file univoco
            String fileExtension = "";
            int dotIndex = cleanFileName.lastIndexOf('.');
            if (dotIndex >= 0) { // Usare >= 0 per gestire file come ".env"
                fileExtension = cleanFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Costruisce il percorso completo e salva il file
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossibile salvare il file " + cleanFileName, ex);
        }
    }

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