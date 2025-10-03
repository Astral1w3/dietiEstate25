package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Integer idAddress;
    private String street;
    private Integer houseNumber;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private MunicipalityDTO municipality;
}
