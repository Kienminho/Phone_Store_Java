package com.tdtu.phone_store_java.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private int quantity;
    private int unitPrice;
    private Date createdDate;

    public InvoiceItem(Invoice invoice, Product product, int quantity, int unitPrice, Date createdDate) {
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdDate = createdDate;
    }
}
