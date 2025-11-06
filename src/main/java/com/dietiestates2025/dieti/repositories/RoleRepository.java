package com.dietiestates2025.dieti.repositories;

import com.dietiestates2025.dieti.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 

public interface RoleRepository extends JpaRepository<Role, String> {

    // MODIFICA: Restituire Optional<Role> invece di Role è una best practice.
    // Permette di gestire il caso "non trovato" in modo esplicito e sicuro
    // nel service, usando .orElseThrow().
    Optional<Role> findByRoleName(String roleName);
    
}