package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.PropertyState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyStateRepository extends JpaRepository<PropertyState, Integer> {
}