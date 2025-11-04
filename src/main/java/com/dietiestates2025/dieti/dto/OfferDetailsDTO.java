package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferDetailsDTO {
    
    // Dati dell'offerta
    private Integer id_offer;
    private BigDecimal offer_price;
    private Date offer_date;
    private String state;

    // Dati della proprietà
    private Integer id_property;
    private String propertyAddress;
    private BigDecimal listingPrice;

    // Dati del cliente
    private String clientName;
    private String clientEmail;
}