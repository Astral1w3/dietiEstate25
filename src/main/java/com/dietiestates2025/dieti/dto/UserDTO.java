package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String email;
    private String username;
    private String userPassword;
    private AgencyDTO agency;
}
