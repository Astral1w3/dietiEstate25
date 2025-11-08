package com.dietiestates2025.dieti.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "property_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyState {

    @Id
    private Integer id;

    private String state;
    
    @JsonIgnore
    @OneToMany(mappedBy = "propertyState")
    private List<Property> properties;
}