package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.exception.FileStorageException;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException; // <-- USA LA TUA ECCEZIONE
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
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${spring.servlet.multipart.location}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new FileStorageException("Cannot store file: file name is invalid.");
        }

        String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(originalFileName));
        
        try {
            if (cleanFileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + cleanFileName);
            }

            String fileExtension = "";
            int dotIndex = cleanFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                fileExtension = cleanFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + cleanFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // --- MODIFICA CHIAVE: Usa la tua eccezione esistente ---
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            // --- MODIFICA CHIAVE: Usa la tua eccezione esistente ---
            throw new ResourceNotFoundException("File not found: " + fileName, ex);
        }
    }
}