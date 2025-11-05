package com.dietiestates2025.dieti.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.data.domain.Page;         // <-- IMPORT AGGIUNTO
import org.springframework.data.domain.Pageable;       // <-- IMPORT AGGIUNTO
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dietiestates2025.dieti.dto.PagedResponseDTO;
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
import org.springframework.transaction.annotation.Transactional;
import com.dietiestates2025.dieti.model.PropertyState;
import com.dietiestates2025.dieti.repositories.PropertyStateRepository;

@Service
public class PropertyService {

    private final PropertyRepository repo;
    private final AddressService addressService;
    private final DozerBeanMapper dozerBeanMapper;
    private final FileStorageService fileStorageService; 
    private final UserRepository userRepository; 
    private final DashboardRepository dashboardRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final PropertyStateRepository propertyStateRepository;

    private static final Integer DEFAULT_PROPERTY_STATE_ID = 1;

    public PropertyService(
        SaleTypeRepository saleTypeRepository,
        PropertyRepository repo, 
        AddressService addressService, 
        DozerBeanMapper dozerBeanMapper,
        FileStorageService fileStorageService,
        UserRepository userRepository,
        DashboardRepository dashboardRepository,
        PropertyStateRepository propertyStateRepository
    ) {
        this.saleTypeRepository = saleTypeRepository;
        this.repo = repo;
        this.addressService = addressService;
        this.dozerBeanMapper = dozerBeanMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.dashboardRepository = dashboardRepository;
        this.propertyStateRepository = propertyStateRepository;
    }

    @Transactional
    public PropertyDTO addPropertyAndImages(PropertyDTO propertyDTO, List<MultipartFile> imageFiles, String userEmail){ 
        // ... (questo metodo rimane invariato)
        User owner = userRepository.findById(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Dashboard userDashboard = dashboardRepository.findById(owner.getEmail()).orElseGet(() -> {
            Dashboard newDashboard = Dashboard.builder().email(owner.getEmail()).user(owner).build();
            return dashboardRepository.save(newDashboard);
        });
        
        Address address = dozerBeanMapper.map(propertyDTO.getAddress(), Address.class);
        Property property = dozerBeanMapper.map(propertyDTO, Property.class);
        property.setAddress(addressService.checkIfAddressExist(address));
        
        String saleTypeName = propertyDTO.getSaleType();
        if (saleTypeName == null || saleTypeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale type ('rent' or 'buy') is required.");
        }

        SaleType saleType = saleTypeRepository.findById(saleTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "SaleType '" + saleTypeName + "' not found. Make sure it exists in the database."
                ));

        Set<SaleType> saleTypes = new HashSet<>();
        saleTypes.add(saleType);
        property.setSaleTypes(saleTypes);

        PropertyState defaultState = propertyStateRepository.findById(DEFAULT_PROPERTY_STATE_ID)
            .orElseThrow(() -> new IllegalStateException(
                "Stato di default 'Available' non trovato. Assicurati che esista con ID " + DEFAULT_PROPERTY_STATE_ID
            ));
        
        property.setPropertyState(defaultState);
        
        Set<Dashboard> dashboards = new HashSet<>();
        dashboards.add(userDashboard);
        property.setDashboards(dashboards); // Ora il tipo corrisponde
        
        Property savedProperty = repo.save(property);

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
    
    public boolean deleteProperty(int propertyId) {
        // ... (questo metodo rimane invariato)
        if(repo.existsById(propertyId)){
            repo.deleteById(propertyId);
            return true;
        }
        return false;
    }

    public PropertyDTO getPropertyById(int propertyId) {
        // ... (questo metodo rimane invariato)
        Property property = repo.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        return mapToDtoWithImageUrls(property);
    }
    
    // Lasciamo questo metodo per compatibilità, se usato altrove
    public List<PropertyDTO> findPropertiesByLocation(String location) {
        // ... (questo metodo rimane invariato)
        // NOTA: Questa chiamata non è ottimizzata né paginata.
        // List<Property> properties = repo.findByLocationIgnoreCase(location);
        // return properties.stream()
        //                  .map(this::mapToDtoWithImageUrls)
        //                  .collect(Collectors.toList());
        return Collections.emptyList(); // Temporaneamente disabilitato per evitare confusione
    }

    private static final Integer AVAILABLE_STATE_ID = 1;

//     // --- INIZIO BLOCCO MODIFICATO ---
//  public Page<PropertyDTO> findPropertiesByLocationAvailable(String location, Pageable pageable) {
//         String lowerCaseLocation = location.toLowerCase();
    
//         // 1. Esegui la prima query per ottenere solo gli ID della pagina corrente.
//         Page<Integer> idsPage = repo.findIdsByLocationAndState(lowerCaseLocation, AVAILABLE_STATE_ID, pageable);
    
//         // Se la pagina non ha contenuto, restituisci subito una pagina vuota.
//         if (!idsPage.hasContent()) {
//             return Page.empty(pageable);
//         }
    
//         // 2. Esegui la seconda query per caricare i dettagli completi SOLO per gli ID trovati.
//         List<Property> properties = repo.findFullPropertiesByIds(idsPage.getContent());
    
//         // 3. Mappa i risultati in DTO.
//         List<PropertyDTO> dtos = properties.stream()
//                              .map(this::mapToDtoWithImageUrls)
//                              .collect(Collectors.toList());
    
//         // 4. Ricostruisci l'oggetto Page<PropertyDTO> finale.
//         return new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
//     }
//     // --- FINE BLOCCO MODIFICATO ---

    private PropertyDTO mapToDtoWithImageUrls(Property property) {
        // ... (questo metodo rimane invariato)
        PropertyDTO dto = dozerBeanMapper.map(property, PropertyDTO.class);
        
        Set<SaleType> saleTypes = property.getSaleTypes();
        if (saleTypes != null && !saleTypes.isEmpty()) {
            dto.setSaleType(saleTypes.iterator().next().getSaleType());
        }

        if (property.getPropertyState() != null) {
            dto.setPropertyState(property.getPropertyState().getState());
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
        // ... (questo metodo rimane invariato)
        Property property = repo.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        PropertyStats stats = property.getPropertyStats();
        if (stats == null) {
            stats = new PropertyStats();
            stats.setProperty(property);
            stats.setNumberOfViews(1);
        } else {
            stats.setNumberOfViews(stats.getNumberOfViews() + 1);
        }
        
        property.setPropertyStats(stats);
        repo.save(property);
    }

     public PagedResponseDTO<PropertyDTO> findPropertiesByLocationAvailable(String location, Pageable pageable) {
        String lowerCaseLocation = location.toLowerCase();
    
        Page<Integer> idsPage = repo.findIdsByLocationAndState(lowerCaseLocation, AVAILABLE_STATE_ID, pageable);
    
        List<PropertyDTO> dtos;
        if (!idsPage.hasContent()) {
            dtos = Collections.emptyList(); // Lista vuota se non ci sono ID
        } else {
            List<Property> properties = repo.findFullPropertiesByIds(idsPage.getContent());
            dtos = properties.stream()
                         .map(this::mapToDtoWithImageUrls)
                         .collect(Collectors.toList());
        }
    
        // --- COSTRUISCI E RESTITUISCI IL NUOVO DTO ---
        return new PagedResponseDTO<>(
            dtos,
            idsPage.getNumber(),
            idsPage.getTotalElements(),
            idsPage.getTotalPages(),
            idsPage.isLast()
        );
    }
}