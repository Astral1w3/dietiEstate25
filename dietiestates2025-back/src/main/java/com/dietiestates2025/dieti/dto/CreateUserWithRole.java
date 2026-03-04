package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserWithRole {
    private String emailCreator;
    private String email;
    private String username;
    private String userPassword;
    private String roleName;
}
