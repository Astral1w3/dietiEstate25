package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.repositories.RoleRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerService extends AgentService {

    @Autowired
    public ManagerService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        
        super(userRepository, roleRepository, passwordEncoder, dozerBeanMapper);
    }
    
}