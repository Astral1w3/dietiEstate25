package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a.street, m.zipCode, p.provinceName FROM Address a JOIN a.municipality m JOIN m.province p")
    List<Object[]> findAllZipCodeAndProvinceName();
    

}