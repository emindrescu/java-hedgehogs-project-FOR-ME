package com.cydeo.javahedgehogsproject.service.implementation;

import com.cydeo.javahedgehogsproject.dto.ClientVendorDto;
import com.cydeo.javahedgehogsproject.dto.CompanyDto;
import com.cydeo.javahedgehogsproject.entity.ClientVendor;
import com.cydeo.javahedgehogsproject.entity.Company;
import com.cydeo.javahedgehogsproject.mapper.MapperUtil;
import com.cydeo.javahedgehogsproject.repository.ClientVendorRepository;
import com.cydeo.javahedgehogsproject.service.ClientVendorService;
import com.cydeo.javahedgehogsproject.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public ClientVendorDto findById(Long id) {
        return mapperUtil.convert(clientVendorRepository.findById(id), new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findAll() {
        CompanyDto companyDto = securityService.getLoggedInCompany();
        Company company = mapperUtil.convert(companyDto, new Company());

        List<ClientVendor> clientVendorList = clientVendorRepository.findAllByCompanyOrderByClientVendorNameAscClientVendorTypeAsc(company);

        return clientVendorList.stream().map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto())).collect(Collectors.toList());
    }

}