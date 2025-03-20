package com.dietiestates2025.dieti.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import com.dietiestates2025.dieti.model.User;

public interface UserRepository extends JpaRepository<User, String> {

}