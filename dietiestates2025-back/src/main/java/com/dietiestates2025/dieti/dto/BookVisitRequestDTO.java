package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookVisitRequestDTO {
    private Integer propertyId;
    private Date visitDate;
}