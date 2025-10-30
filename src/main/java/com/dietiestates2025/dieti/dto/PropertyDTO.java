package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private Integer idProperty;
    private BigDecimal price;
    private String description;
    private Double squareMeters;
    private Integer numberOfRooms;
    private String saleType;
    private String energyClass;
    private AddressDTO address;  
    private List<ServiceDTO> services;  
    private List<SaleTypeDTO> saleTypes;
    //private PropertyStatsDTO propertyStats;
    //private List<BuyingAndSellingDTO> buyingAndSellings;
    private List<DashboardDTO> dashboards;
    private List<BookedVisitDTO> bookedVisits;
    private List<String> imageUrls;
}
