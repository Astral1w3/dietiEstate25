package com.dietiestates2025.dieti.model;

import java.util.List;

import jakarta.persistence.OneToMany;

public class Agent extends User{

    @OneToMany
    private List<BuyingAndSelling> BuyingAndSellings;
    
}
