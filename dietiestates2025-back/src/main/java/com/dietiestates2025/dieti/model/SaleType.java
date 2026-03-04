package com.dietiestates2025.dieti.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SaleType {

    @Id
    private String saleType;

    @JsonIgnore
    @ManyToMany(mappedBy = "saleTypes")
    private List<Property> properties;

    

    
}
