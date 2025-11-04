package com.dietiestates2025.dieti.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder; // <-- IMPORTA ANCHE @Builder
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // <-- 1. AGGIUNGI L'ANNOTAZIONE @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    // --- 2. DEFINISCI I CAMPI CORRETTI ---

    // Dati per le StatCard
    private long totalViews;
    private long bookedVisits;
    private long offersReceived;
    private long activeListings;

    // Dati per il grafico a linee
    private Map<String, Long> salesOverTime;

    // Dati per la tabella delle proprietà
    private List<PropertyDashboardDTO> properties;
}