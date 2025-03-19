package com.dietiestates2025.dieti.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyingAndSellingDTO {

    private Integer idSale;
    private Date saleDate;
    private BigDecimal salePrice;
    private String emailAgent;
    private String emailBuyer;
    
}
