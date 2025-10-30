package com.dietiestates2025.dieti.dto;

import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    private String email;
    private String currentPassword;
    private String newPassword;
}