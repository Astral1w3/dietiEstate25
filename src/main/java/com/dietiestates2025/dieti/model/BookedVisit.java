package com.dietiestates2025.dieti.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedVisit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBooking;

    private Date visitDate;

    @ManyToOne
    @JoinColumn(name = "id_property", referencedColumnName = "idProperty")
    private Property property;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    private User user;
}