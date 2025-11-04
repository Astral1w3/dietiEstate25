package com.dietiestates2025.dieti.dto;

import java.util.Date;
import lombok.Data;
import lombok.Builder;

@Data
@Builder // Usiamo @Builder per una costruzione più facile e leggibile
public class BookingDetailsDTO {

    private Integer id_booking;
    private Date visit_date;
    
    // Dettagli della proprietà
    private Integer id_property;
    private String propertyAddress;

    // Dettagli del cliente che ha prenotato
    private String email;
    private String clientName;

    // Aggiungiamo anche lo stato della visita, se lo implementerai in futuro
    // private String status; 
}