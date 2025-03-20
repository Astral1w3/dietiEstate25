package com.dietiestates2025.dieti.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.dto.UserDTO;

@Component
public class UserController extends AbstractRoleController {

    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    public void print(){
        System.out.println("UserController");
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String userEmail){
        UserDTO userDTO = userService.getUserByEmail(userEmail);
        return ResponseEntity.ok(userDTO);
    }
    
    
}
