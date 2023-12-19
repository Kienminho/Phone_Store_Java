package com.tdtu.phone_store_java.Model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Entity
@NoArgsConstructor
@Table
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String roleName;

}
