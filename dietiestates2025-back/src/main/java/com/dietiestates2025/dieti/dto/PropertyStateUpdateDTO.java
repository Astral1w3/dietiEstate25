package com.dietiestates2025.dieti.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStateUpdateDTO {

    @NotBlank(message = "can't be null")
    private String state;

}