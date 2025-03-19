package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyingAndSelling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSale;

    private Date saleDate;
    private BigDecimal salePrice;
    private String emailAgent;
    private String emailBuyer;

    /*@ManyToOne
    @JoinColumn(name = "id_agent", referencedColumnName = "idUser", nullable = false)
    private User agent;

    @ManyToOne
    @JoinColumn(name = "id_buyer", referencedColumnName = "idUser", nullable = false)
    private User buyer;
    */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_property", referencedColumnName = "idProperty", nullable = false)
    private Property property;

}