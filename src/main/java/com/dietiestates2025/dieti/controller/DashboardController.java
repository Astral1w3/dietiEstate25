// package com.dietiestates2025.dieti.controller;

// import com.dietiestates2025.dieti.Service.PropertyService;
// import com.dietiestates2025.dieti.dto.DashboardDTO;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/dashboard")
// public class DashboardController {

//     private final PropertyService propertyService;

//     public DashboardController(PropertyService propertyService) {
//         this.propertyService = propertyService;
//     }

//     @GetMapping("/agent")
//     public ResponseEntity<DashboardDTO> getAgentDashboard(Authentication authentication) {
//         // L'email dell'utente autenticato viene usata per recuperare i dati corretti
//         String userEmail = authentication.getName(); 
//         DashboardDTO dashboardData = propertyService.getDashboardDataForAgent(userEmail);
//         return ResponseEntity.ok(dashboardData);
//     }
// }