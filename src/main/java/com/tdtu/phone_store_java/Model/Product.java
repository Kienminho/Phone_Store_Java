package com.tdtu.phone_store_java.Model;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String barCode;
    private String name;
    private String screenSize;
    private String ram;
    private String rom;
    private int importPrice;
    private int priceSale;
    private String description;
    private String imageLink;
    private int saleNumber;
    private Date createdDate;
    @Nullable
    private Date updatedDate;
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(String barCode, String name, String screenSize, String ram, String rom, int importPrice, int priceSale, String description, String imageLink, int saleNumber, Date createdDate, boolean isDeleted, Category category) {
        this.barCode = barCode;
        this.name = name;
        this.screenSize = screenSize;
        this.ram = ram;
        this.rom = rom;
        this.importPrice = importPrice;
        this.priceSale = priceSale;
        this.description = description;
        this.imageLink = imageLink;
        this.saleNumber = saleNumber;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
        this.category = category;
    }
}
