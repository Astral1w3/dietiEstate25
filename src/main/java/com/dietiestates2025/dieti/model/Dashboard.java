package com.dietiestates2025.dieti.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {
    @Id
    private String email;

    private int numberOfSales;
    //private int numberOfOffers;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "email")
    private User user;

    @JsonIgnore
    @ManyToMany(mappedBy = "dashboards")
    List<Property> properties;
}