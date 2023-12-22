package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.DTO.InvoiceItemDTO;
import com.tdtu.phone_store_java.Model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    @Query("SELECT new com.tdtu.phone_store_java.DTO.InvoiceItemDTO(inv.invoiceCode,p.name,i.quantity,i.unitPrice, i.createdDate) FROM InvoiceItem i JOIN i.invoice inv JOIN i.product p WHERE inv.invoiceCode=:id")
    List<InvoiceItemDTO> getDetailInvoice(@Param("id") String id);
}
