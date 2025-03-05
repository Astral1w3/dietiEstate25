package com.dietiestates2025.dieti.mapper;

import com.dietiestates2025.dieti.DTO.AddressDTO;
import com.dietiestates2025.dieti.DTO.MunicipalityDTO;
import com.dietiestates2025.dieti.DTO.PropertyDTO;
import com.dietiestates2025.dieti.DTO.ProvinceDTO;
import com.dietiestates2025.dieti.DTO.RegionDTO;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.Province;
import com.dietiestates2025.dieti.model.Region;

public class PropertyMapper {
    
    public static PropertyDTO toDTO(Property property) {
        if (property == null) return null;

        return new PropertyDTO(
            property.getIdProperty(),
            property.getPrice(),
            property.getDescription(),
            property.getSquareMeters(),
            property.getNumberOfRooms(),
            property.getSaleType(),
            property.getEnergyClass(),
            toDTO(property.getAddress())  // Converte Address in AddressDTO
        );
    }

        private static AddressDTO toDTO(Address address) {
        if (address == null) return null;

        return new AddressDTO(
            address.getIdAddress().longValue(),
            address.getStreet(),
            address.getHouseNumber(),
            toDTO(address.getMunicipality())  // Converte Municipality in MunicipalityDTO
        );
    }

    private static MunicipalityDTO toDTO(Municipality municipality) {
        if (municipality == null) return null;

        return new MunicipalityDTO(
            municipality.getMunicipalityName(),
            municipality.getZipCode(),
            toDTO(municipality.getProvince())  // Converte Province in ProvinceDTO
        );
    }

    private static ProvinceDTO toDTO(Province province) {
        if (province == null) return null;

        return new ProvinceDTO(
            province.getProvinceName(),
            province.getAcronym(),
            toDTO(province.getRegion())  // Converte Region in RegionDTO
        );
    }

    private static RegionDTO toDTO(Region region) {
        if (region == null) return null;

        return new RegionDTO(region.getRegionName());
    }
}

