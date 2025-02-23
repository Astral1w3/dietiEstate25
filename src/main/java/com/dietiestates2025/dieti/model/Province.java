package com.dietiestates2025.dieti.model;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Province {

    @Id
    @Column(name = "acronym", length = 2, nullable = false)
    private String acronym;

    @Column(name = "province_name", length = 100, nullable = false)
    private String provinceName;

    @Column(name = "num_municipality", nullable = false)
    private int numbersOfMunicipality;

    @ManyToOne
    @JoinColumn(name = "id_region", referencedColumnName = "id_region", nullable = false)
    private Region region;



    @OneToMany(mappedBy = "province")
    private List<Municipality> municipalities;

    
}
