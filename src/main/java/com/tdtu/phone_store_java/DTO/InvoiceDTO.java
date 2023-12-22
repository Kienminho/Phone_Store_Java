package com.tdtu.phone_store_java.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@NoArgsConstructor
@Setter
@Getter
public class InvoiceDTO {
    private String invoiceCode;
    private String salePeople;
    private String customerName;
    private int receiveMoney;
    private int excessMoney;
    private int totalMoney;
    private int quantity;
    private Date createdDate;

    @JsonCreator
    public InvoiceDTO(String invoiceCode,String salePeople, String customerName, int receiveMoney, int excessMoney, int totalMoney, int quantity, Date createdDate) {
        this.invoiceCode = invoiceCode;
        this.salePeople = salePeople;
        this.customerName = customerName;
        this.receiveMoney = receiveMoney;
        this.excessMoney = excessMoney;
        this.totalMoney = totalMoney;
        this.quantity = quantity;
        this.createdDate = createdDate;
    }
}
