package com.dietiestates2025.dieti.controller;

import org.springframework.stereotype.Component;

@Component
public class AdminController extends ManagerController{
    public void print(){
        System.out.println("AdminController");
    }
}
