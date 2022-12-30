package com.cydeo.javahedgehogsproject.controller;

import com.cydeo.javahedgehogsproject.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {

       private final CategoryService categoryService;

       public CategoryController(CategoryService categoryService) {
              this.categoryService = categoryService;
       }

       @GetMapping("/list")
       public String retrieveAllCategories(Model model){

              model.addAttribute("categories", categoryService.listAllCategoriesByUser());

              return "category/category-list";
       }

}