package com.dietiestates2025.dieti.factory;

import org.springframework.stereotype.Component;

import com.dietiestates2025.dieti.controller.AbstractRoleController;
import com.dietiestates2025.dieti.controller.AdminController;
import com.dietiestates2025.dieti.controller.AgentController;
import com.dietiestates2025.dieti.controller.ManagerController;
import com.dietiestates2025.dieti.controller.UnregisteredUserController;
import com.dietiestates2025.dieti.controller.UserController;
import com.dietiestates2025.dieti.model.User;

@Component
public class ControllerFactory {
    
    private AdminController adminController;
    private ManagerController managerController;
    private AgentController agentController;
    private UserController userController;
    private UnregisteredUserController unregisteredUserController;

    public ControllerFactory(AgentController agentController, AdminController adminController, UserController userController, ManagerController managerController, UnregisteredUserController unregisteredUserController) {
        this.adminController = adminController;
        this.managerController = managerController;
        this.agentController = agentController;
        this.userController = userController;
        this.unregisteredUserController = unregisteredUserController;
    }

    public AbstractRoleController getController(User user) {
        switch(user.getRole()) {
            case "Admin": 
                return adminController;
            case "Manager": 
                return managerController;
            case "Agent": 
                return agentController;
            case "User":
                return userController;
            default: 
                return unregisteredUserController;
        }
    }
}

