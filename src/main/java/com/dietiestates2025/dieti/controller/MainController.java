package com.dietiestates2025.dieti.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.dietiestates2025.dieti.factory.ControllerFactory;

public class MainController {

    private final ControllerFactory controllerFactory;

    @Autowired
    public MainController(ControllerFactory controllerFactory){
        this.controllerFactory = controllerFactory;
    }


    
}
