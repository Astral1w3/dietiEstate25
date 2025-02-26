package com.dietiestates2025.dieti.controller;
import org.springframework.stereotype.Component;

@Component
public class ManagerController extends AgentController{
    public void print(){
        System.out.println("ManagerController");
    }
}
