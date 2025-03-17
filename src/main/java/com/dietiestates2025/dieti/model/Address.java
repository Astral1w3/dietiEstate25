package com.dietiestates2025.dieti.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAddress;

    private String street;
    private Integer houseNumber;


    @ManyToOne
    @JoinColumn(name = "zip_code", referencedColumnName = "zip_code", nullable = false)
    private Municipality municipality;
    
    @JsonIgnore
    @OneToMany(mappedBy = "address")
    private List<Property> properties;

    @Override
    public String toString() {
        return "Address{" +
                "idAddress=" + idAddress +
                ", street='" + street + '\'' +
                ", houseNumber=" + houseNumber +
                ", municipalityZipCode='" + (municipality != null ? municipality.getZipCode() : "null") + '\'' +
                '}';
    }
    

}