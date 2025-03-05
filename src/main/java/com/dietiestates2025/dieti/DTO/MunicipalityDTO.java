package com.dietiestates2025.dieti.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityDTO {
    private String municipalityName;
    private String zipCode;
    private ProvinceDTO province;
}

