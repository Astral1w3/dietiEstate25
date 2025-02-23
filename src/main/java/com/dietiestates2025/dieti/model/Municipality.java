package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "acronym", referencedColumnName = "acronym", nullable = false)
    private Province province;


    @OneToMany(mappedBy = "municipality")
    private List<Address> addresses;
}