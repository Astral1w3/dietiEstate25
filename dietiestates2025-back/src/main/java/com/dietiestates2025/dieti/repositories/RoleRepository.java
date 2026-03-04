package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByRoleName(String roleName);
    
}