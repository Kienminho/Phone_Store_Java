package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.categoryName =:name")
    Category getCategoryByName(@Param("name") String name);
}
