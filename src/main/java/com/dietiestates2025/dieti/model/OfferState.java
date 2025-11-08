package com.dietiestates2025.dieti.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class OfferState {

    @Id
    private Integer id;
    
    private String state;


    @JsonIgnore
    @OneToMany(mappedBy = "offerState")
    private List<Offer> offers;
}