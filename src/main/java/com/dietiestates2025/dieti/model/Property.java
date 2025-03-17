package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
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
    private String saleType;
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

    @Override
    public String toString() {
        return "Property{" +
                "idProperty=" + idProperty +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", squareMeters=" + squareMeters +
                ", numberOfRooms=" + numberOfRooms +
                ", saleType='" + saleType + '\'' +
                ", energyClass='" + energyClass + '\'' +
                ", addressId=" + (address != null ? address.getIdAddress() : "null") +
                ", services=" + (services != null ? services.stream()
                    .map(Service::getServiceName)
                    .collect(Collectors.toList()) : "null") +
                '}';
    }

}

