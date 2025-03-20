package com.dietiestates2025.dieti.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedVisitDTO {

    private Integer idBooking;
    private Date visitDate;
    
}
