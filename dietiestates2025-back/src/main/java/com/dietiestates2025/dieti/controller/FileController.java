package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.FileStorageService;
import com.dietiestates2025.dieti.exception.*;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.repositories.ImageRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileStorageService fileStorageService;
    private final PropertyRepository propertyRepository;
    private final ImageRepository imageRepository;

    public FileController(FileStorageService fss, PropertyRepository pr, ImageRepository ir) {
        this.fileStorageService = fss;
        this.propertyRepository = pr;
        this.imageRepository = ir;
    }

    @PostMapping("/properties/{propertyId}/images")
    public ResponseEntity<String> uploadImage(@PathVariable int propertyId, @RequestParam("file") MultipartFile file) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        String fileName = fileStorageService.storeFile(file);

        Image image = new Image(fileName, property);
        imageRepository.save(image);
        
        String fileDownloadUri = buildFileUri(fileName);

        return ResponseEntity.ok(fileDownloadUri);
    }
    
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private String buildFileUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();
    }
}