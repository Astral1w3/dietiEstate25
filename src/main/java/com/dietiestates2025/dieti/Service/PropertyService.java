package com.dietiestates2025.dieti.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Dashboard;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.PropertyStats;
import com.dietiestates2025.dieti.model.SaleType;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import com.dietiestates2025.dieti.repositories.SaleTypeRepository;
import com.dietiestates2025.dieti.exception.*;

import jakarta.transaction.Transactional;

@Service
public class PropertyService {

    private final PropertyRepository repo;
    private final AddressService addressService;
    private final DozerBeanMapper dozerBeanMapper;
    private final FileStorageService fileStorageService; 
    private final UserRepository userRepository; 
    private final DashboardRepository dashboardRepository;
    private final SaleTypeRepository saleTypeRepository; // <-- Dipendenza per SaleType

    // --- COSTRUTTORE (già corretto) ---
    public PropertyService(
        SaleTypeRepository saleTypeRepository,
        PropertyRepository repo, 
        AddressService addressService, 
        DozerBeanMapper dozerBeanMapper,
        FileStorageService fileStorageService,
        UserRepository userRepository,
        DashboardRepository dashboardRepository
    ) {
        this.saleTypeRepository = saleTypeRepository;
        this.repo = repo;
        this.addressService = addressService;
        this.dozerBeanMapper = dozerBeanMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Transactional
    public PropertyDTO addPropertyAndImages(PropertyDTO propertyDTO, List<MultipartFile> imageFiles, String userEmail){ 
        User owner = userRepository.findById(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Dashboard userDashboard = dashboardRepository.findById(owner.getEmail()).orElseGet(() -> {
            Dashboard newDashboard = Dashboard.builder().email(owner.getEmail()).user(owner).build();
            return dashboardRepository.save(newDashboard);
        });
        
        Address address = dozerBeanMapper.map(propertyDTO.getAddress(), Address.class);
        Property property = dozerBeanMapper.map(propertyDTO, Property.class);
        property.setAddress(addressService.checkIfAddressExist(address));
        
        // --- INIZIO LOGICA CORRETTA PER SALETYPE ---

        // 1. Ottieni il nome del tipo di vendita (es. "rent" o "buy") dal DTO.
        //    Assicurati che il tuo PropertyDTO abbia un campo String `saleType`.
        String saleTypeName = propertyDTO.getSaleType();
        if (saleTypeName == null || saleTypeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale type ('rent' or 'buy') is required.");
        }

        // 2. Cerca l'entità SaleType nel database usando il nome come ID.
        SaleType saleType = saleTypeRepository.findById(saleTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "SaleType '" + saleTypeName + "' not found. Make sure it exists in the database."
                ));
        
        List<SaleType> saleTypes = new ArrayList<>();
        saleTypes.add(saleType);
        property.setSaleTypes(saleTypes);
        
        List<Dashboard> dashboards = new ArrayList<>();
        dashboards.add(userDashboard);
        property.setDashboards(dashboards);
        // Salva la proprietà con tutte le sue relazioni (indirizzo, saleType, dashboard)
        Property savedProperty = repo.save(property);

        // Gestione delle immagini (invariata)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Image> images = imageFiles.stream().map(file -> {
                String fileName = fileStorageService.storeFile(file);
                return new Image(fileName, savedProperty);
            }).collect(Collectors.toList());
            savedProperty.getImages().addAll(images);
        }
        
        Property finalProperty = repo.save(savedProperty);
        return mapToDtoWithImageUrls(finalProperty);
    }
    
    // --- IL RESTO DELLA CLASSE È INVARIATO ---

    public boolean deleteProperty(int propertyId) {
        if(repo.existsById(propertyId)){
            repo.deleteById(propertyId);
            return true;
        }
        return false;
    }

    public PropertyDTO getPropertyById(int propertyId) {
        Property property = repo.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        return mapToDtoWithImageUrls(property);
    }

    public List<PropertyDTO> findPropertiesByLocation(String location) {
        List<Property> properties = repo.findByLocationIgnoreCase(location);
        return properties.stream()
                         .map(this::mapToDtoWithImageUrls)
                         .collect(Collectors.toList());
    }

    private PropertyDTO mapToDtoWithImageUrls(Property property) {
        PropertyDTO dto = dozerBeanMapper.map(property, PropertyDTO.class);
        
        // --- MAPPIAMO ANCHE SALETYPE PER INVIARLO AL FRONTEND ---
        // Se la lista non è vuota, prendiamo il primo (e unico) elemento
        if (property.getSaleTypes() != null && !property.getSaleTypes().isEmpty()) {
            dto.setSaleType(property.getSaleTypes().get(0).getSaleType());
        }

        List<String> imageUrls = property.getImages().stream()
            .map(image -> ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(image.getFileName())
                    .toUriString())
            .collect(Collectors.toList());
            
        dto.setImageUrls(imageUrls);
        return dto;
    }
    @Transactional
    public void incrementViewCount(Integer propertyId) {
        Property property = repo.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        PropertyStats stats = property.getPropertyStats();
        if (stats == null) {
            // Se non esistono statistiche, creale
            stats = new PropertyStats();
            stats.setProperty(property);
            stats.setNumberOfViews(1);
        } else {
            // Incrementa il contatore
            stats.setNumberOfViews(stats.getNumberOfViews() + 1);
        }
        
        property.setPropertyStats(stats);
        repo.save(property);
    }
}