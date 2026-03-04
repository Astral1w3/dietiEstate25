package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class PropertyDashboardDTO {
    private Integer idProperty;
    private String fullAddress;
    private String saleType;
    private String propertyState;
    private BigDecimal price;
    private String mainImageUrl;
    private long viewCount;
    private long bookedVisitsCount;
    private long offersCount;
}