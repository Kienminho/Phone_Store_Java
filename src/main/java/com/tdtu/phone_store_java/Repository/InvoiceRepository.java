package com.tdtu.phone_store_java.Repository;

import com.tdtu.phone_store_java.DTO.InvoiceDTO;
import com.tdtu.phone_store_java.Model.Customer;
import com.tdtu.phone_store_java.Model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT new com.tdtu.phone_store_java.DTO.InvoiceDTO(i.invoiceCode,u.fullName, c.fullName,i.receiveMoney," +
            "i.excessMoney,i.totalMoney,i.quantity,i.createdDate) " +
            "FROM Invoice i JOIN i.customer c JOIN i.salesStaff u WHERE c.Id =:id")
    List<InvoiceDTO> findAllWithCustomerAndSalesStaff(@Param("id") Long id);

    @Query("SELECT new com.tdtu.phone_store_java.DTO.InvoiceDTO(i.invoiceCode,u.fullName, c.fullName,i.receiveMoney," +
            "i.excessMoney,i.totalMoney,i.quantity,i.createdDate) " +
            "FROM Invoice i JOIN i.customer c JOIN i.salesStaff u WHERE i.createdDate BETWEEN :startDate AND :endDate")
    List<InvoiceDTO> findDateByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
