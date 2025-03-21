package com.dietiestates2025.dieti.Service;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.repositories.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final DozerBeanMapper dozerBeanMapper;

    public UserService(UserRepository userRepository, DozerBeanMapper dozerBeanMapper){
        this.userRepository = userRepository;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    public UserDTO getUserByEmail(String Email){
        return dozerBeanMapper.map(userRepository.findById(Email).get(), UserDTO.class);
    }
    
}
