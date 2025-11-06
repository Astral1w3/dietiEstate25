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

/**
 * Service class for handling file storage operations, such as storing and loading files.
 * This service ensures that files are stored securely in a configured directory.
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    /**
     * Constructs the FileStorageService and initializes the file storage directory.
     * It creates the directory if it does not already exist.
     *
     * @param uploadDir The path to the upload directory, injected from application properties.
     * @throws FileStorageException if the storage directory could not be created.
     */
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

    /**
     * Stores an uploaded file to the configured storage location.
     * The method sanitizes the original filename, generates a unique filename using UUID
     * to prevent naming conflicts and hide original file names, and then saves the file.
     *
     * @param file The {@link MultipartFile} object representing the file to be stored.
     * @return The unique, randomly generated filename under which the file has been saved.
     * @throws FileStorageException if the filename is invalid or if an I/O error occurs during storage.
     */
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
     * It verifies that the requested file path is within the designated storage directory before serving it.
     *
     * @param fileName The name of the file to load.
     * @return A {@link Resource} object for the requested file, which can be sent to the client.
     * @throws ResourceNotFoundException if the file is not found, is not readable, or if the path is outside the storage directory.
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            // Resolve the filename against the base storage directory.
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

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