package com.cydeo.javahedgehogsproject.service;


import com.cydeo.javahedgehogsproject.dto.CategoryDto;


import java.util.List;

public interface CategoryService {

    CategoryDto findById(long id);

    List<CategoryDto> retrieveCategoryByCompany();

    List<CategoryDto> listAllCategoriesByUser();


}