package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor; // <-- IMPORTA QUESTO
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // <-- IMPORTA QUESTO

@Data
@Builder
@NoArgsConstructor    // <-- AGGIUNGI QUESTA ANNOTAZIONE
@AllArgsConstructor 
public class PropertyDashboardDTO {
    private Integer idProperty;
    private String fullAddress;
    private String saleType;
    private String propertyState;
    private BigDecimal price;
    private String mainImageUrl;
    // Conteggi aggregati per questa proprietà
    private long viewCount;
    private long bookedVisitsCount;
    private long offersCount;
}