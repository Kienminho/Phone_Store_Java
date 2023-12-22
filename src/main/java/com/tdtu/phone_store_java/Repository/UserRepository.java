package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.DTO.UserDTO;
import com.tdtu.phone_store_java.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName <> 'admin'")
    List<User> getAllUser();
    @Query("SELECT u FROM User u where u.email =:email")
    User getUserByEmail(@Param("email") String email);
    @Query("SELECT new com.tdtu.phone_store_java.DTO.UserDTO(u.Id, u.userName,u.password,u.fullName," +
            "u.email,u.address,u.phoneNumber,u.avatar,r.roleName, u.activationToken, u.isActivated,u.firstLogin,u.isDeleted) FROM User u JOIN u.roles r WHERE u.userName =:username")
    UserDTO getUserByName(@Param("username") String username);
    @Query("SELECT u FROM User u where u.Id =:id")
    User getUserById(@Param("id") Long id);
    @Query("SELECT new com.tdtu.phone_store_java.DTO.UserDTO(u.Id, u.userName,u.fullName," +
            "u.email,u.address,u.phoneNumber,u.avatar,r.roleName) FROM User u JOIN u.roles r WHERE u.Id =:id")
    UserDTO getInfoMine(@Param("id") Long id);
            @Query("SELECT u FROM User u where u.activationToken =:token and u.isDeleted = false and u.isActivated = false ")
    User getUserByToken(@Param("token") String token);
}
