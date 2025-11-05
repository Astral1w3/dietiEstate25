package com.dietiestates2025.dieti.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "offer") // Assicuriamoci che il nome della tabella sia esatto
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOffer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date offerDate;
    
    private BigDecimal offerPrice;

    @ManyToOne
    // La colonna per la proprietà sembra essere 'id_property'
    @JoinColumn(name = "id_property") 
    private Property property;

    @JsonIgnore
    @ManyToOne
    // La colonna per l'utente è 'email'
    @JoinColumn(name = "email") 
    private User user;

    // --- INIZIO DELLA CORREZIONE PRINCIPALE ---

    @ManyToOne
    // Diciamo a JPA di usare la colonna 'id_offer_state' per gestire la relazione.
    // Questa è la colonna che il DB richiede come NOT NULL.
    @JoinColumn(name = "id_offer_state", referencedColumnName = "id", nullable = false)
    private OfferState offerState;
    
}