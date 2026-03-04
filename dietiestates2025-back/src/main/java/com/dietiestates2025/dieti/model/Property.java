package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.HashSet; // <-- Import necessario
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Buona pratica per entità JPA
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // L'identità dell'oggetto è basata sul suo ID
    private Integer idProperty;

    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;
    private Double squareMeters;
    private Integer numberOfRooms;
    private String energyClass;

    @ManyToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "id_address", referencedColumnName = "idAddress",  nullable = false)
    @ToString.Exclude // Evita problemi di ricorsione nei log
    private Address address;

    // --- INIZIO CORREZIONI: INIZIALIZZAZIONE DI TUTTE LE COLLEZIONI ---

    @ManyToMany
    @JoinTable(
        name = "property_service", 
        joinColumns = @JoinColumn(name = "id_property"), 
        inverseJoinColumns = @JoinColumn(name = "service_name")
    )
    @Builder.Default // Fondamentale quando si usa @Builder
    @ToString.Exclude
    private Set<Service> services = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "property_saletype",
        joinColumns = @JoinColumn(name = "id_property"),
        inverseJoinColumns = @JoinColumn(name = "sale_type")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<SaleType> saleTypes = new HashSet<>();

    @OneToMany(mappedBy = "property")
    @Builder.Default
    @ToString.Exclude
    private Set<BuyingAndSelling> buyingAndSellings = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "property_dashboard",
        joinColumns = @JoinColumn(name = "id_property"),
        inverseJoinColumns = @JoinColumn(name = "email")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Dashboard> dashboards = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "property")
    @Builder.Default
    @ToString.Exclude
    private Set<BookedVisit> bookedVisits = new HashSet<>(); // Corretto nome variabile e tipo

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Cambiato EAGER a LAZY
    @Builder.Default
    private Set<Image> images = new HashSet<>(); // <-- QUESTA È LA CORREZIONE SPECIFICA PER IL TUO ERRORE

    // --- FINE CORREZIONI ---

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private PropertyStats propertyStats;

    @ManyToOne
    @JoinColumn(name = "id_property_state", nullable = false)
    @ToString.Exclude
    private PropertyState propertyState; 
}