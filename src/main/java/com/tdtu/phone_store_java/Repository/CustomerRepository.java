package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.phoneNumber = :phoneNumber")
    Customer getCustomerByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
