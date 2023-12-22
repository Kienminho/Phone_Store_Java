package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.idSalePeople =:id")
    List<Cart> getAll(@Param("id") Long id);

    @Query("SELECT c FROM Cart c WHERE c.Id =:id")
    Cart getCartItem(@Param("id")Long id);

    @Query("SELECT c FROM Cart c WHERE c.idSalePeople =:id")
    List<Cart> getCartsByIdSalePeople(@Param("id")Long id);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.idSalePeople =:id")
    void deleteCartBySalePeople(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.Id =:id")
    void deleteCartById(@Param("id") Long id);
}
