package com.dietiestates2025.dieti.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Municipality;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a.street, m.zipCode, p.provinceName FROM Address a JOIN a.municipality m JOIN m.province p")
    List<Object[]> findAllZipCodeAndProvinceName();

    Optional<Address> findByStreetAndHouseNumberAndMunicipality(String street, Integer houseNumber, Municipality municipality);


}