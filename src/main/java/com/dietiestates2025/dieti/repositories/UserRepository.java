package com.dietiestates2025.dieti.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dietiestates2025.dieti.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}