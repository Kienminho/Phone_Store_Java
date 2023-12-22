package com.tdtu.phone_store_java.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long Id;
    private String userName;
    private String password;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String avatar;
    private String activationToken;
    private String roleName;
    private Boolean isActivated;
    private Boolean firstLogin;
    private Boolean isDeleted;

    @JsonCreator
    public UserDTO(Long id, String userName, String fullName, String email, String address, String phoneNumber, String avatar, String roleName) {
        Id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.roleName = roleName;
    }
}