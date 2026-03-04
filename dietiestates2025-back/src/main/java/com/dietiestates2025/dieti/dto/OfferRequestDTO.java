package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OfferRequestDTO {
    
    @NotNull(message = "L'ID della proprietà è obbligatorio")
    private Integer propertyId;
    
    @NotNull(message = "Il prezzo dell'offerta è obbligatorio")
    @Positive(message = "Il prezzo dell'offerta deve essere positivo")
    private BigDecimal offerPrice;
}