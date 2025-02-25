package com.dietiestates2025.dieti.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Region {

    @Id
    @Column(name = "id_region", nullable = false)
    private Integer regionId;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @OneToMany(mappedBy = "region", fetch = FetchType.EAGER)
    private List<Province> provinces;
    
}
