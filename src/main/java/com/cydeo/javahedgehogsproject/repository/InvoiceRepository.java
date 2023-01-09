package com.cydeo.javahedgehogsproject.repository;

import com.cydeo.javahedgehogsproject.entity.Company;
import com.cydeo.javahedgehogsproject.entity.Invoice;
import com.cydeo.javahedgehogsproject.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Long countAllByInvoiceTypeAndCompanyId(InvoiceType invoiceType, Long companyId);

    List<Invoice> findInvoicesByCompanyAndInvoiceTypeOrderByInvoiceNoDesc(Company company, InvoiceType invoiceType);

}
