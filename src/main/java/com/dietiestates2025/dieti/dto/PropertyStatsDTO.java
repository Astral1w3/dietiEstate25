package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStatsDTO {

    private Integer idProperty;
    private int numberOfViews;
    private int numberOfScheduledVisits;
    
}
