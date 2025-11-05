package com.dietiestates2025.dieti.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder; 
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private long totalViews;
    private long bookedVisits;
    private long offersReceived;
    private long activeListings;

    private Map<String, Long> salesOverTime;

    private List<PropertyDashboardDTO> properties;
}