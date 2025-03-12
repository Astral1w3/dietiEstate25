package com.dietiestates2025.dieti.DTO;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Integer idAddress;
    private String street;
    private int houseNumber;
    private MunicipalityDTO municipality;
    
}
