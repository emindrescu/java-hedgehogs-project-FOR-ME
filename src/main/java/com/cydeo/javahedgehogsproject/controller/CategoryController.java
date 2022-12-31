package com.cydeo.javahedgehogsproject.controller;

import com.cydeo.javahedgehogsproject.dto.CategoryDto;
import com.cydeo.javahedgehogsproject.dto.CompanyDto;
import com.cydeo.javahedgehogsproject.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

       private final CategoryService categoryService;

       public CategoryController(CategoryService categoryService) {
              this.categoryService = categoryService;
       }

       @GetMapping("/list")
       public String retrieveAllCategories(Model model) {

              model.addAttribute("categories", categoryService.listAllCategoriesByUser());

              return "category/category-list";
       }


       @GetMapping("/create")
       public String createCategory(Model model) {

              model.addAttribute("newCategory", new CategoryDto());

              return "/category/category-create";

       }

       @PostMapping("/create")
       public String insertCategory(@Valid @ModelAttribute("newCategory") CategoryDto category, BindingResult bindingResult) {

              if (bindingResult.hasErrors())

                     return "/category/category-create";

              categoryService.save(category);

              return "redirect:/categories/list";

       }

       @GetMapping("/update/{id}")
       public String updateCategory(@PathVariable("id") Long id, Model model) {
              model.addAttribute("category", categoryService.findById(id));

              CategoryDto categoryDto=categoryService.findById(id);
              if(categoryDto.isHasProduct()){
                     return "/category/category-update";
              }
              return "/category/category-update";
       }

       @PostMapping("/update/{id}")
       public String editCategory(@Valid @ModelAttribute("category") CategoryDto category, BindingResult bindingResult, Model model, @PathVariable("id") Long id) {

              if (bindingResult.hasErrors()) {
                     return "/category/category-update";
              }

              categoryService.save(category);
              return "redirect:/categories/list";
       }



       @GetMapping("/delete/{id}")
       public String deleteCategory(@PathVariable("id")Long id) {


              categoryService.delete(id);
              return "redirect:/categories/list";

       }

       }

