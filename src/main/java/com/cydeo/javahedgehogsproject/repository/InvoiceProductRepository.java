package com.cydeo.javahedgehogsproject.repository;

import com.cydeo.javahedgehogsproject.entity.Company;
import com.cydeo.javahedgehogsproject.entity.InvoiceProduct;
import com.cydeo.javahedgehogsproject.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    List<InvoiceProduct> findAllByInvoiceId(Long invoice_id);

    List<InvoiceProduct> findAllByInvoice_CompanyAndInvoiceId(Company company, Long id);

    List<InvoiceProduct> findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc(InvoiceStatus invoiceStatus, Company company);

}
