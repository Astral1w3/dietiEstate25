package com.dietiestates2025.dieti.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) per ricevere la richiesta di aggiornamento 
 * dello stato di una proprietà dal client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStateUpdateDTO {

    /**
     * Il nuovo stato da impostare per la proprietà (es. "AVAILABLE", "OCCUPIED").
     * Non può essere nullo o vuoto.
     */
    @NotBlank(message = "can't be null")
    private String state;

}