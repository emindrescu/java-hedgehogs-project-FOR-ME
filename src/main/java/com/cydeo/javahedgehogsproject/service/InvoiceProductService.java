package com.cydeo.javahedgehogsproject.service;

import com.cydeo.javahedgehogsproject.dto.InvoiceDto;
import com.cydeo.javahedgehogsproject.dto.InvoiceProductDto;
import com.cydeo.javahedgehogsproject.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceProductService {

    BigDecimal totalTax(Long invoiceId);

    BigDecimal totalPriceWithoutTax(Long invoiceId);
    InvoiceService findAllByInvoice(Long id);
    List<InvoiceProductDto> findAllInvoiceProducts(Long invoiceId);
    List<InvoiceProductDto> findAllById(Long id);

    void saveByInvoiceId(InvoiceProductDto invoiceProduct, Long id);

    void delete(Long productId);
    void deleteByInvoice(InvoiceType invoiceType, InvoiceDto invoiceDto);
}
