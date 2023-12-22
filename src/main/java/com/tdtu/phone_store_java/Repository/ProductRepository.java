package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.DTO.ProductDTO;
import com.tdtu.phone_store_java.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new com.tdtu.phone_store_java.DTO.ProductDTO(p.Id, p.barCode,p.name,p.screenSize,p.ram,p.rom,p.importPrice,p.priceSale," +
            "p.description,p.imageLink,p.saleNumber,p.createdDate,p.updatedDate,p.isDeleted," +
            "c.categoryName) FROM Product p JOIN p.category c WHERE p.isDeleted = false")
    List<ProductDTO> getAllProducts();

    @Query("SELECT p FROM Product p WHERE p.barCode =:barCode")
    Product getProductByBarCode(@Param("barCode")String barCode);

    @Query("SELECT new com.tdtu.phone_store_java.DTO.ProductDTO(p.Id, p.barCode,p.name,p.screenSize,p.ram,p.rom,p.importPrice,p.priceSale," +
            "p.description,p.imageLink,p.saleNumber,p.createdDate,p.updatedDate,p.isDeleted," +
            "c.categoryName) FROM Product p JOIN p.category c WHERE p.barCode LIKE CONCAT('%', :keyword,'%') OR p.name LIKE CONCAT('%', :keyword,'%')")
    List<ProductDTO> getProductByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.Id =:id and p.isDeleted = false")
    Product findProductById(@Param("id") Long id);
}
