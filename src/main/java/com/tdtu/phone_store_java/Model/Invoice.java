package com.tdtu.phone_store_java.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Fetch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String invoiceCode;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User salesStaff;
    private int receiveMoney;
    private int excessMoney;
    private int totalMoney;
    private int quantity;
    private Date createdDate;

    public Invoice(String invoiceCode, Customer customer, User salesStaff, int receiveMoney, int excessMoney, int totalMoney, int quantity, Date createdDate) {
        this.invoiceCode = invoiceCode;
        this.customer = customer;
        this.salesStaff = salesStaff;
        this.receiveMoney = receiveMoney;
        this.excessMoney = excessMoney;
        this.totalMoney = totalMoney;
        this.quantity = quantity;
        this.createdDate = createdDate;
    }
}
