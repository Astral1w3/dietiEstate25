package com.dietiestates2025.dieti.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProperty;

    private int numberOfViews;
    private int numberOfScheduledVisits;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_property", referencedColumnName = "idProperty")
    private Property property;

}
