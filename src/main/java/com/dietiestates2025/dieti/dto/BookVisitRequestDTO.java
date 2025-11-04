package com.dietiestates2025.dieti.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BookVisitRequestDTO {
    private Integer propertyId;
    private Date visitDate;
}