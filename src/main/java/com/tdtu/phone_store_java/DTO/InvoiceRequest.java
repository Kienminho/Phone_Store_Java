package com.tdtu.phone_store_java.DTO;

import com.tdtu.phone_store_java.Model.Cart;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceRequest {
    private String phoneNumber;
    private String fullName;
    private String address;
    private int quantity;
    private int totalMoney;
    private int receiveMoney;
    private int moneyBack;
    private List<Cart> items;
}

