package com.dietiestates2025.dieti.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImage;

    // Salviamo solo il nome del file generato. È più sicuro e flessibile.
    @Column(nullable = false, unique = true)
    private String fileName;

    // Relazione con la Proprietà
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProperty", nullable = false)
    @JsonIgnore // FONDAMENTALE per evitare cicli infiniti quando si converte in JSON
    private Property property;

    // Costruttore utile
    public Image(String fileName, Property property) {
        this.fileName = fileName;
        this.property = property;
    }
}