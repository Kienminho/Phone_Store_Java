package com.tdtu.phone_store_java.Service;

import com.tdtu.phone_store_java.Common.Utils;
import com.tdtu.phone_store_java.DTO.ProductDTO;
import com.tdtu.phone_store_java.Model.Category;
import com.tdtu.phone_store_java.Model.Product;
import com.tdtu.phone_store_java.Repository.CategoryRepository;
import com.tdtu.phone_store_java.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ProductService  {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Product addProduct(ProductDTO req, String imageLink) {
        try {
            String categoryName = req.getCategoryName();
            Category c = categoryRepository.getCategoryByName(categoryName);
            String barCode = String.valueOf(Utils.generateSixDigitNumber());
            String name = req.getName();
            String screenSize = req.getScreenSize();
            String ram = req.getRam() + " GB";
            String rom = req.getRom() + " GB";
            int importPrice = req.getImportPrice();
            int priceSale = req.getPriceSale();
            String desc = req.getDescription();
            Product newProduct = new Product(barCode, name,screenSize,ram,rom,importPrice,priceSale,desc,imageLink,0,new Date(),false, c);
            productRepository.save(newProduct);
            return newProduct;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
