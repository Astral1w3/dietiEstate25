package com.dietiestates2025.dieti.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String email;
    private String name;
    private String googleId;
}