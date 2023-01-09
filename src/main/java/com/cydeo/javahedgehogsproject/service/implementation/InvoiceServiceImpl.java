package com.cydeo.javahedgehogsproject.service.implementation;

import com.cydeo.javahedgehogsproject.dto.InvoiceDto;
import com.cydeo.javahedgehogsproject.entity.Company;
import com.cydeo.javahedgehogsproject.entity.Invoice;
import com.cydeo.javahedgehogsproject.enums.InvoiceType;
import com.cydeo.javahedgehogsproject.mapper.MapperUtil;
import com.cydeo.javahedgehogsproject.repository.InvoiceProductRepository;
import com.cydeo.javahedgehogsproject.repository.InvoiceRepository;
import com.cydeo.javahedgehogsproject.repository.ProductRepository;
import com.cydeo.javahedgehogsproject.service.InvoiceProductService;
import com.cydeo.javahedgehogsproject.service.InvoiceService;
import com.cydeo.javahedgehogsproject.service.SecurityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    final private SecurityService securityService;
    private final ProductRepository productRepository;
    private final InvoiceProductService invoiceProductService;
    private final InvoiceProductRepository invoiceProductRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, SecurityService securityService, ProductRepository productRepository, InvoiceProductService invoiceProductService, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.productRepository = productRepository;
        this.invoiceProductService = invoiceProductService;
        this.invoiceProductRepository = invoiceProductRepository;
    }


    @Override
    public InvoiceDto findById(long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) {
        Long companyId = securityService.getLoggedInUser().getCompany().getId();
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public String InvoiceNo(InvoiceType invoiceType, Long companyId) {

        Long id = invoiceRepository.countAllByInvoiceTypeAndCompanyId(invoiceType, companyId);
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" + "00" + (id + 1);
        }

        return InvoiceNo;
    }

    @Override
    public List<InvoiceDto> findAllInvoice(InvoiceType invoiceType) {
        Company company = mapperUtil.convert(securityService.getLoggedInCompany(), new Company());
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceTypeOrderByInvoiceNoDesc(company, invoiceType);

        return invoiceList.stream().map(invoice -> {

            InvoiceDto invoiceDTO = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDTO.setInvoiceProducts(invoiceProductService.findAllInvoiceProducts(invoice.getId()));
            invoiceDTO.setTax(invoiceProductService.totalTax(invoice.getId()));
            invoiceDTO.setPrice(invoiceProductService.totalPriceWithoutTax(invoice.getId()));
            invoiceDTO.setTotal(invoiceDTO.getTax().add(invoiceDTO.getPrice()));

            return invoiceDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
        //delete all invoiceProducts belongs to the deleted invoice:
        invoiceProductService.deleteByInvoice(InvoiceType.SALES,mapperUtil.convert(invoice, new InvoiceDto()));
    }


}
