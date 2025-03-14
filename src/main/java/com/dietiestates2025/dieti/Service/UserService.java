package com.dietiestates2025.dieti.Service;

import org.springframework.stereotype.Service;

import com.dietiestates2025.dieti.controller.AbstractRoleController;
import com.dietiestates2025.dieti.factory.ControllerFactory;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.UserRepository;

@Service
public class UserService {
    
    private final ControllerFactory controllerFactory;
    private final UserRepository userRepository;

    public UserService(ControllerFactory controllerFactory, UserRepository userRepository){
        this.controllerFactory = controllerFactory;
        this.userRepository = userRepository;
    }

    public void assignController(User user){
        if(user != null){
            AbstractRoleController controller = controllerFactory.getController(user);
            user.setController(controller);
        }
    }

    public User getUserByMail(String mail){
        User u = userRepository.findByEmail(mail);
        assignController(u);
        return u;
    }
    
    
}
