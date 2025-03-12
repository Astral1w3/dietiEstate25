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
                address.getIdAddress(),
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

    public static Property toEntity(PropertyDTO propertyDTO) {
        if (propertyDTO == null) return null;

        Property property = new Property();
        property.setIdProperty(propertyDTO.getIdProperty());
        property.setPrice(propertyDTO.getPrice());
        property.setDescription(propertyDTO.getDescription());
        property.setSquareMeters(propertyDTO.getSquareMeters());
        property.setNumberOfRooms(propertyDTO.getNumberOfRooms());
        property.setSaleType(propertyDTO.getSaleType());
        property.setEnergyClass(propertyDTO.getEnergyClass());
        property.setAddress(toEntity(propertyDTO.getAddress())); // Converte AddressDTO in Address

        return property;
    }

    private static Address toEntity(AddressDTO addressDTO) {
        if (addressDTO == null) return null;

        Address address = new Address();
        if (addressDTO.getIdAddress() != null) {
            address.setIdAddress(addressDTO.getIdAddress());
        }
        address.setStreet(addressDTO.getStreet());
        address.setHouseNumber(addressDTO.getHouseNumber());
        address.setMunicipality(toEntity(addressDTO.getMunicipality())); // Converte MunicipalityDTO in Municipality

        return address;
    }

    private static Municipality toEntity(MunicipalityDTO municipalityDTO) {
        if (municipalityDTO == null) return null;

        Municipality municipality = new Municipality();
        municipality.setMunicipalityName(municipalityDTO.getMunicipalityName());
        municipality.setZipCode(municipalityDTO.getZipCode());
        municipality.setProvince(toEntity(municipalityDTO.getProvince())); // Converte ProvinceDTO in Province

        return municipality;
    }

    private static Province toEntity(ProvinceDTO provinceDTO) {
        if (provinceDTO == null) return null;

        Province province = new Province();
        province.setProvinceName(provinceDTO.getProvinceName());
        province.setAcronym(provinceDTO.getAcronym());
        province.setRegion(toEntity(provinceDTO.getRegion())); // Converte RegionDTO in Region

        return province;
    }

    private static Region toEntity(RegionDTO regionDTO) {
        if (regionDTO == null) return null;

        Region region = new Region();
        region.setRegionName(regionDTO.getRegionName());

        return region;
    }
}

