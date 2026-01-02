package com.fashionshop.controller.client;

import com.fashionshop.model.Address;
import com.fashionshop.model.User;
import com.fashionshop.service.AddressService;
import com.fashionshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account/addresses")
public class ClientAddressController {

    @Autowired private AddressService addressService;
    @Autowired private UserService userService;

    @GetMapping
    public String viewAddresses(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userService.findByEmail(principal.getName());
        List<Address> addresses = addressService.findByUser(user);
        
        model.addAttribute("addresses", addresses);
        return "client/account/addresses";
    }

    @PostMapping("/add")
    public String addAddress(@ModelAttribute Address address, 
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            User user = userService.findByEmail(principal.getName());
            address.setUser(user);
            
            addressService.save(address);
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/account/addresses";
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, Principal principal) {
        addressService.deleteAddress(id);
        return "redirect:/account/addresses";
    }
    
    @GetMapping("/set-default/{id}")
    public String setDefault(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        
        addressService.setDefaultAddress(id, user.getId());
        
        return "redirect:/account/addresses";
    }
    
    
    @PostMapping("/update")
    public String updateAddress(@ModelAttribute Address address, 
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            User user = userService.findByEmail(principal.getName());
            
            addressService.updateAddress(user.getId(), address);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/account/addresses";
    }
}