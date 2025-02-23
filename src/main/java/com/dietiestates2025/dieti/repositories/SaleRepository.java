package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Integer> {

    @Query("SELECT s.salePrice, a.street FROM Sale s JOIN s.property p JOIN p.address a")
    List<Object[]> findAllPriceAndStreet();

}