package com.dietiestates2025.dieti.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Province {
    @Id
    @GeneratedValue
    private String acronym;
    private String provinceName;
    private int numbersOfMunicipality;
    private int idRegion;


}