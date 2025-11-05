package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.exception.FileStorageException;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
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
        // Ensure the base directory path is absolute and normalized.
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            // Create the directory if it doesn't exist.
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) { // Be more specific with the exception
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Get the original filename from the multipart request.
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check for invalid characters or path sequences.
        if (originalFileName.isEmpty() || originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains an invalid path sequence or is empty: " + originalFileName);
        }

        try {
            // Securely generate a unique filename to prevent overwrites and hide internal naming.
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Resolve the path against the base location. The filename is system-generated and safe.
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            
            // Copy the file to the target location.
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * Loads a file as a resource and performs security checks to prevent path traversal attacks.
     *
     * @param fileName The name of the file to load.
     * @return A Resource object for the requested file.
     * @throws ResourceNotFoundException if the file is not found or is outside the storage directory.
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            // Resolve the filename against the base storage directory.
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            // --- SECURITY CHECK ---
            // This is the critical security check to prevent path traversal.
            // It verifies that the resolved path is still within the intended storage directory.
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new ResourceNotFoundException("Cannot access file outside of the designated storage directory: " + fileName);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or is not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found (Malformed URL): " + fileName, ex);
        }
    }
}