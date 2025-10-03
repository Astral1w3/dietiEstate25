package com.dietiestates2025.dieti.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferState {

    /**
     * Il nome dello stato (es. "IN ATTESA", "ACCETTATA").
     * Funge da chiave primaria (PK) perché è univoco e descrittivo.
     */
    @Id
    private String state;

    /**
     * Lista di tutte le offerte che si trovano in questo stato.
     * Questa è la parte "inversa" della relazione e potrebbe non essere sempre necessaria,
     * ma è una buona pratica definirla.
     * Usiamo @JsonIgnore per evitare cicli infiniti durante la serializzazione JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "offerState")
    private List<Offer> offers;
}