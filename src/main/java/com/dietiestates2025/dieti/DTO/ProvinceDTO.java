package com.dietiestates2025.dieti.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    private String provinceName;
    private String acronym;
    private RegionDTO region;
}
