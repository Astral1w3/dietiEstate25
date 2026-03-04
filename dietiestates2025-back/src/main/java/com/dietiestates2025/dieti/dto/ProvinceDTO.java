package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    private String acronym;
    private String provinceName;
    private RegionDTO region;
}
