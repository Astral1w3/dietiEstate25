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

    @Column(nullable = false, unique = true)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProperty", nullable = false)
    @JsonIgnore
    private Property property;

    public Image(String fileName, Property property) {
        this.fileName = fileName;
        this.property = property;
    }
}