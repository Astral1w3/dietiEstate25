package com.dietiestates2025.dieti.model;

import java.util.Collections;
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
    @JoinColumn(name = "id_agency", referencedColumnName = "idAgency")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "role_name")
    private Role role;

    @OneToMany(mappedBy = "agent")
    private List<BuyingAndSelling> sellings;

    @OneToMany(mappedBy = "buyer")
    private List<BuyingAndSelling> buyings;

    @OneToOne(mappedBy = "user")
    private Dashboard dashboard;

    @OneToMany(mappedBy = "user")
    private List<BookedVisit> bookedVisits;

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

    public List<BuyingAndSelling> getSellings() {
        String roleName = this.role.getRoleName().toUpperCase();
        if (roleName.equals("AGENT") || roleName.equals("ADMIN") || roleName.equals("MANAGER")) {
            return sellings;
        }
        return Collections.emptyList();
    }
    
    public List<BuyingAndSelling> getBuyings() {
        if ("USER".equalsIgnoreCase(this.role.getRoleName())) {
            return buyings;
        }
        return Collections.emptyList();
    }

}