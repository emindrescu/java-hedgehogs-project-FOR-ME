package com.cydeo.javahedgehogsproject.controller;

import com.cydeo.javahedgehogsproject.dto.ClientVendorDto;
import com.cydeo.javahedgehogsproject.enums.ClientVendorType;
import com.cydeo.javahedgehogsproject.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String getClientVendorsList(Model model) {
        model.addAttribute("clientVendors", clientVendorService.findAll());
        return "/clientVendor/clientVendor-list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model){
        model.addAttribute("newClientVendor", new ClientVendorDto());
//        model.addAttribute("clientVendorTypes", ClientVendorType.values());
//        model.addAttribute("countries",);
        return "/clientVendor/clientVendor-create";
    }

    @PostMapping("/create")
    public String insertClientVendor(@ModelAttribute("newClientVendor")ClientVendorDto clientVendorDto){
        clientVendorService.create(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id){
        clientVendorService.deleteById(id);
        return "redirect:/clientVendors/list";
    }


}
