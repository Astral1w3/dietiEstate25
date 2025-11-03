package com.dietiestates2025.dieti.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dietiestates2025.dieti.model.Dashboard;

public interface DashboardRepository extends JpaRepository<Dashboard, String> {

    Optional<Dashboard> findByUserEmail(String email);
}