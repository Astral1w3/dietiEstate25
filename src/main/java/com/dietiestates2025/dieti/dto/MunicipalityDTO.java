package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityDTO {
    private String zipCode;
    private String municipalityName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private ProvinceDTO province;
}
