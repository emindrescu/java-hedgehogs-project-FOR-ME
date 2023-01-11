package com.cydeo.javahedgehogsproject.service.implementation;

import com.cydeo.javahedgehogsproject.dto.CompanyDto;
import com.cydeo.javahedgehogsproject.dto.InvoiceDto;
import com.cydeo.javahedgehogsproject.entity.Company;
import com.cydeo.javahedgehogsproject.entity.Invoice;
import com.cydeo.javahedgehogsproject.enums.InvoiceStatus;
import com.cydeo.javahedgehogsproject.enums.InvoiceType;
import com.cydeo.javahedgehogsproject.mapper.MapperUtil;
import com.cydeo.javahedgehogsproject.repository.InvoiceRepository;
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
    private final SecurityService securityService;
    private final InvoiceProductService invoiceProductService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, SecurityService securityService, InvoiceProductService invoiceProductService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public InvoiceDto findById(long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        return mapperUtil.convert(invoice, new InvoiceDto());
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
    public String generateInvoiceNoForPurchase(InvoiceType invoiceType, Long companyId) {

        Long id = invoiceRepository.countAllByInvoiceTypeAndCompanyId(invoiceType, companyId);
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" +  String.format("%03d", id + 1);
        }

        return InvoiceNo;
    }

    @Override
    public String createInvoiceNoForSalesInvoice(InvoiceType invoiceType, Long companyId) {
        Long id = invoiceRepository.countAllByInvoiceTypeAndCompanyId(invoiceType, companyId);

        String saleInvoiceNo = "";

        if (invoiceType.getValue().equals("Sales")) {
            saleInvoiceNo = "S-" + "00" + (id + 1);
        }

        return saleInvoiceNo;
    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) {
        Long companyId = securityService.getLoggedInUser().getCompany().getId();
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(generateInvoiceNoForPurchase(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto getNewSalesInvoice(InvoiceType invoiceType) {

        Long companyId = securityService.getLoggedInUser().getCompany().getId();
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(createInvoiceNoForSalesInvoice(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public void savePurchaseInvoice(InvoiceDto purchaseInvoice) {
        CompanyDto company = securityService.getLoggedInCompany();

        purchaseInvoice.setCompany(company);
        purchaseInvoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        purchaseInvoice.setInvoiceType(InvoiceType.PURCHASE);

        Invoice newInvoice = mapperUtil.convert(purchaseInvoice, new Invoice());

        invoiceRepository.save(newInvoice);
        purchaseInvoice.setId(newInvoice.getId());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto) {

        CompanyDto company = securityService.getLoggedInCompany();
        invoiceDto.setCompany(company);
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(InvoiceType.SALES);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoiceRepository.save(invoice);
        invoiceDto.setId(invoice.getId());

        return invoiceDto;
    }

    @Override
    public void deletePurchaseInvoice(Long id) {

        Invoice invoice = invoiceRepository.findById(id).get();
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
        invoiceProductService.deleteByInvoice(InvoiceType.PURCHASE, mapperUtil.convert(invoice, new InvoiceDto()));
    }

    @Override
    public void update(InvoiceDto invoice) {
        Invoice dbInvoice = invoiceRepository.findById(invoice.getId()).get();
        Invoice convertedInvoice = mapperUtil.convert(invoice, new Invoice());
        convertedInvoice.setId(dbInvoice.getId());
        convertedInvoice.setInvoiceStatus(dbInvoice.getInvoiceStatus());
        convertedInvoice.setInvoiceType(dbInvoice.getInvoiceType());
        convertedInvoice.setCompany(dbInvoice.getCompany());
        invoiceRepository.save(convertedInvoice);
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
        //delete all invoiceProducts belongs to the deleted invoice:
        invoiceProductService.deleteByInvoice(InvoiceType.SALES, mapperUtil.convert(invoice, new InvoiceDto()));
    }

    @Override
    public void approvePurchaseInvoice(Long purchaseInvoiceId) {

        Invoice invoice = invoiceRepository.findById(purchaseInvoiceId).get();
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setDate(LocalDate.now());
        invoiceProductService.approvePurchaseInvoice(purchaseInvoiceId);
        invoiceRepository.save(invoice);

    }

    @Override
    public void approveSalesInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        if (invoiceProductService.checkQuantityAmount(invoiceId)) {
            invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
            invoice.setDate(LocalDate.now());
            invoiceProductService.calculateProfitLossForSale(invoiceId);
            invoiceRepository.save(invoice);
        } else {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDto.setHasNotEnoughProductQuantity(true);
        }

    }

    @Override
    public List<InvoiceDto> findAllApprovedInvoice(InvoiceStatus invoiceStatus) {
        Company company = mapperUtil.convert(securityService.getLoggedInCompany(), new Company());
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceStatusOrderByInvoiceNoDesc(company, invoiceStatus);

        return invoiceList.stream().map(invoice -> {

            InvoiceDto invoiceDTO = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDTO.setInvoiceProducts(invoiceProductService.findAllInvoiceProducts(invoice.getId()));
            invoiceDTO.setTax(invoiceProductService.totalTax(invoice.getId()));
            invoiceDTO.setPrice(invoiceProductService.totalPriceWithoutTax(invoice.getId()));
            invoiceDTO.setTotal(invoiceDTO.getTax().add(invoiceDTO.getPrice()));

            return invoiceDTO;
        }).collect(Collectors.toList());
    }

}



