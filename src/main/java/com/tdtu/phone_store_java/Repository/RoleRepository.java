package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT u FROM Role u WHERE u.Id = :id")
    Role getRoleById(@Param("id") Long id);
}
