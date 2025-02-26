package com.dietiestates2025.dieti.model;

import com.dietiestates2025.dieti.controller.AbstractRoleController;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "usertable")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String email;

    private String username;
    private String userPassword;

    @ManyToOne
    @JoinColumn(name = "id_agency")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "role_name")
    private Role role;

    //@OneToMany
    //private List<BuyingAndSelling> BuyingAndSellings;

    @Transient
    private AbstractRoleController controller;

    public User(String email, String username, String userPassword, Agency agency, Role role){
        this.email = email;
        this.username = username;
        this.userPassword = userPassword;
        this.agency = agency;
        this.role = role;
    }


    public String getRole(){
        return role.getRoleName();
    }

}

