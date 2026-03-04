package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.BuyingAndSelling;

public interface BuyingAndSellingRepository extends JpaRepository<BuyingAndSelling, Integer> {

    @Query("SELECT b.salePrice, a.street FROM BuyingAndSelling b JOIN b.property p JOIN p.address a")
    List<Object[]> findAllPriceAndStreet();

}