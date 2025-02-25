package com.dietiestates2025.dieti.Factory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dietiestates2025.dieti.Controller.AdminController;
import com.dietiestates2025.dieti.Controller.AgentController;
import com.dietiestates2025.dieti.Controller.ManagerController;
import com.dietiestates2025.dieti.Controller.UnregisteredUserController;
import com.dietiestates2025.dieti.Controller.UserController;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.Controller.RoleController;

@Component
public class ControllerFactory {
    
    private AdminController adminController;
    private ManagerController managerController;
    private AgentController agentController;
    private UserController userController;
    private UnregisteredUserController unregisteredUserController;

    @Autowired
    public ControllerFactory(AgentController agentController, AdminController adminController, UserController userController, ManagerController managerController, UnregisteredUserController unregisteredUserController) {
        this.adminController = adminController;
        this.managerController = managerController;
        this.agentController = agentController;
        this.userController = userController;
        this.unregisteredUserController = unregisteredUserController;
    }

    public RoleController getController(User user) {
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

