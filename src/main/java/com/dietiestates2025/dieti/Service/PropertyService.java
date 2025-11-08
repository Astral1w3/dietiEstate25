package com.dietiestates2025.dieti.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Crea una nuova proprietà e gestisce l'upload delle immagini associate in un'unica transazione.
     * Se una qualsiasi operazione fallisce (es. salvataggio immagine), l'intera creazione viene annullata.
     *
     * @param propertyDTO DTO con i dati della nuova proprietà.
     * @param imageFiles Lista di file immagine da associare.
     * @param userEmail Email dell'utente proprietario, ottenuta dal contesto di sicurezza.
     * @return Il {@link PropertyDTO} della proprietà appena creata, completo di URL delle immagini.
     */
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
        property.setDashboards(dashboards);
        
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
    
   /**
     * Aggiorna lo stato di una proprietà in modo efficiente, utilizzando una query di UPDATE diretta
     * invece di caricare l'intera entità in memoria.
     *
     * @param propertyId L'ID della proprietà da aggiornare.
     * @param newStateName Il nome del nuovo stato (es. "Sold", "Unavailable").
     */
    @Transactional
    public void updatePropertyState(int propertyId, String newStateName) {
        PropertyState newState = propertyStateRepository.findByStateIgnoreCase(newStateName)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Stato '" + newStateName + "' non valido o non trovato nel database."
            ));

        repo.updatePropertyState(propertyId, newState.getId());
    }

    
    @Transactional
    public void deleteProperty(int propertyId) {
        if (!repo.existsById(propertyId)) {
            throw new ResourceNotFoundException("Proprietà non trovata con ID: " + propertyId);
        }
        repo.deleteById(propertyId);
    }

    public PropertyDTO getPropertyById(int propertyId) {
        Property property = repo.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        return mapToDtoWithImageUrls(property);
    }
    
    public List<PropertyDTO> findPropertiesByLocation(String location) {
        return Collections.emptyList();
    }

    private static final Integer AVAILABLE_STATE_ID = 1;

    /**
     * Metodo helper privato per mappare un'entità {@link Property} a un {@link PropertyDTO}.
     * La sua responsabilità principale è quella di convertire la lista di entità Image
     * in una lista di URL assoluti e accessibili dal frontend.
     *
     * @param property L'entità da mappare.
     * @return Il DTO pronto per essere inviato come risposta JSON.
     */
    private PropertyDTO mapToDtoWithImageUrls(Property property) {
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
    
    /**
     * Incrementa il contatore delle visualizzazioni per una data proprietà.
     * Gestisce la creazione dell'entità PropertyStats se non esiste.
     * @param propertyId L'ID della proprietà visualizzata.
     */
    @Transactional
    public void incrementViewCount(Integer propertyId) {
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

    /**
     * Esegue una ricerca paginata e ottimizzata per le proprietà disponibili in una data località.
     * L'ottimizzazione consiste in due query separate: la prima per gli ID, la seconda per i dati completi.
     *
     * @param location La località in cui cercare (es. "Napoli").
     * @param pageable Oggetto che contiene le informazioni di paginazione (numero pagina, dimensione).
     * @return Un {@link PagedResponseDTO} contenente la lista di proprietà per la pagina corrente e i metadati di paginazione.
     */
    public PagedResponseDTO<PropertyDTO> findPropertiesByLocationAvailable(String location, Pageable pageable) {
        String lowerCaseLocation = location.toLowerCase();
    
        Page<Integer> idsPage = repo.findIdsByLocationAndState(lowerCaseLocation, AVAILABLE_STATE_ID, pageable);
    
        List<PropertyDTO> dtos;
        if (!idsPage.hasContent()) {
            dtos = Collections.emptyList();
        } else {
            List<Property> properties = repo.findFullPropertiesByIds(idsPage.getContent());
            dtos = properties.stream()
                         .map(this::mapToDtoWithImageUrls)
                         .collect(Collectors.toList());
        }
    
        return new PagedResponseDTO<>(
            dtos,
            idsPage.getNumber(),
            idsPage.getTotalElements(),
            idsPage.getTotalPages(),
            idsPage.isLast()
        );
    }
}