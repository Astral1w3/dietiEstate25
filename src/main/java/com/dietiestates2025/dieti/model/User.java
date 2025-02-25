package com.dietiestates2025.dieti.model;

import java.util.List;

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

    public String getRole(){
        return role.getRoleName();
    }

}

