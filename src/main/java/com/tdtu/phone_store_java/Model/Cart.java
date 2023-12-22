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
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Long idSalePeople;
    private Long idProduct;
    private String name;
    private String imageLink;
    private int salePrice;
    private int quantity;
    private int totalMoney;

    public Cart(Long idSalePeople, Long idProduct, String name, String imageLink, int salePrice, int quantity, int totalMoney) {
        this.idSalePeople = idSalePeople;
        this.idProduct = idProduct;
        this.name = name;
        this.imageLink = imageLink;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.totalMoney = totalMoney;
    }
}
