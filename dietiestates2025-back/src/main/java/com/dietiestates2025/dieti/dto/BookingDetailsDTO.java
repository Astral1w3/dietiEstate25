package com.dietiestates2025.dieti.dto;

import java.util.Date;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class BookingDetailsDTO {

    private Integer id_booking;
    private Date visit_date;
    
    private Integer id_property;
    private String propertyAddress;

    private String email;
    private String clientName;

}