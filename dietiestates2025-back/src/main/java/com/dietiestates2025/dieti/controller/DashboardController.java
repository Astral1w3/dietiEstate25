package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.DashboardService;
import com.dietiestates2025.dieti.dto.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint protetto per recuperare tutti i dati aggregati per la dashboard
     * dell'agente autenticato.
     * 
     * @param authentication Oggetto fornito da Spring Security con i dettagli dell'utente.
     * @return Un DTO completo con tutte le statistiche della dashboard.
     */
    @GetMapping("/agent")
    public ResponseEntity<DashboardDTO> getAgentDashboard(Authentication authentication) {
        String agentEmail = authentication.getName();
        DashboardDTO dashboardData = dashboardService.getDashboardDataForAgent(agentEmail);
        return ResponseEntity.ok(dashboardData);
    }
}