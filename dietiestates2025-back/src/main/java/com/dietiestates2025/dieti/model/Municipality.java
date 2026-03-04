package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private BigDecimal latitude;
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "acronym", referencedColumnName = "acronym", nullable = false)
    private Province province;

    @JsonIgnore
    @OneToMany(mappedBy = "municipality")
    private List<Address> addresses;

    @Override
    public String toString() {
        return "Municipality{" +
                "zipCode='" + zipCode + '\'' +
                ", municipalityName='" + municipalityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", provinceAcronym='" + (province != null ? province.getAcronym() : "null") + '\'' +
                '}';
    }
}