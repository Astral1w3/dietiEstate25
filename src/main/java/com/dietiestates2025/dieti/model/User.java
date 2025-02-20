package com.dietiestates2025.dieti.model;

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
}

