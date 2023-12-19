package com.tdtu.phone_store_java.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String userName;
    private String password;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String avatar;
    private String activationToken;
    private Date activationExpires;
    private Boolean isActivated = false;
    private Boolean firstLogin = true;
    private Boolean isDeleted = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    public User(String userName, String password, Role role, String fullName, String email, String address, String phoneNumber, String avatar, String activationToken, Date activationExpires) {
        this.userName = userName;
        this.password = password;
        this.roles = new HashSet<>(Collections.singletonList(role));
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.activationToken = activationToken;
        this.activationExpires = activationExpires;

    }
}
