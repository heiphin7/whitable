package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password; // encoded with bcrypt

    @Column
    private String refreshToken;

    @Column
    private String photoUrl;

    @Column
    private Boolean isAdmin;
}
