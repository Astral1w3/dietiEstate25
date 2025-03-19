package com.dietiestates2025.dieti.model;

import java.util.List;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent extends User {

    @OneToMany(mappedBy = "agent")
    private List<BuyingAndSelling> Sellings;
    
}
