package com.tdtu.phone_store_java.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String phoneNumber;
    private String fullName;
    private String address;

    public Customer(String phoneNumber, String fullName, String address) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.address = address;
    }
}
