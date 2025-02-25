package com.dietiestates2025.dieti.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.dietiestates2025.dieti.Factory.ControllerFactory;

public class MainController {

    private final ControllerFactory controllerFactory;

    @Autowired
    public MainController(ControllerFactory controllerFactory){
        this.controllerFactory = controllerFactory;
    }


    
}
