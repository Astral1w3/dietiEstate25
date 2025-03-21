package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProperty;

    private BigDecimal price;
    private String description;
    private Double squareMeters;
    private Integer numberOfRooms;
    private String energyClass;

    
    @ManyToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "id_address", referencedColumnName = "idAddress",  nullable = false)
    private Address address;


    @ManyToMany
    @JoinTable(
    name = "property_service", 
    joinColumns = @JoinColumn(name = "id_property"), 
    inverseJoinColumns = @JoinColumn(name = "service_name"))
    private List<Service> services;


    @ManyToMany
    @JoinTable(
        name = "property_saletype",
        joinColumns = @JoinColumn(name = "id_property"),
        inverseJoinColumns = @JoinColumn(name = "sale_type")
    )
    private List<SaleType> saleTypes;

    @OneToOne(mappedBy = "property")
    private PropertyStats propertyStats;

    @OneToMany(mappedBy = "property")
    private List<BuyingAndSelling> buyingAndSellings;

    @ManyToMany
    @JoinTable(
        name = "property_dashboard",
        joinColumns = @JoinColumn(name = "id_property"),
        inverseJoinColumns = @JoinColumn(name = "email")
    )
    private List<Dashboard> dashboards;

    @JsonIgnore
    @OneToMany(mappedBy = "property")
    private List<BookedVisit> BookedVisits;

    @OneToMany(mappedBy = "property")
    private List<Image> images;

}

