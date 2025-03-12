package com.dietiestates2025.dieti.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.dietiestates2025.dieti.model.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Service> Services;
}
