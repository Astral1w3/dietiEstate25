package com.dietiestates2025.dieti.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dietiestates2025.dieti.model.BookedVisit;

public interface VisitBookingRepository extends JpaRepository<BookedVisit, Integer> {

}