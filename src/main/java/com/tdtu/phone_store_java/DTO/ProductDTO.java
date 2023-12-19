package com.tdtu.phone_store_java.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {
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
    private Date updatedDate;
    private boolean isDeleted;
    private String categoryName;

    @JsonCreator
    public ProductDTO(String name, String screenSize, String ram, String rom, int importPrice,
                      int priceSale, String description, int saleNumber, String categoryName) {
        this.name = name;
        this.screenSize = screenSize;
        this.ram = ram;
        this.rom = rom;
        this.importPrice = importPrice;
        this.priceSale = priceSale;
        this.description = description;
        this.saleNumber = saleNumber;
        this.categoryName = categoryName;
    }

}
