package com.dietiestates2025.dieti.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    
    @JsonIgnore
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<Province> provinces;
    
    @Override
    public String toString() {
    return "Region{id=" + regionId + ", name='" + regionName + "'}"; // Exclude the province list
    }

}
