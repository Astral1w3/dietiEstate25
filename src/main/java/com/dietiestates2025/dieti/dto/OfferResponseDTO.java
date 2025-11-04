package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class OfferResponseDTO {
    private Integer idOffer;
    private Date offerDate;
    private BigDecimal offerPrice;
    private String status;
    private Integer propertyId;
    private String userEmail;
}