package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipality {
    @Id
    @Column(name = "zip_code", length = 5)
    private String zipCode;

    private String municipalityName;
    @Column(length = 2, updatable=false, insertable=false)
    private String acronym;
    private BigDecimal latitude;
    private BigDecimal longitude;

    //@JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "acronym", referencedColumnName = "acronym", nullable = false)
    private Province province;

    @JsonIgnore
    //@JsonBackReference
    @OneToMany(mappedBy = "municipality")
    private List<Address> addresses;
}