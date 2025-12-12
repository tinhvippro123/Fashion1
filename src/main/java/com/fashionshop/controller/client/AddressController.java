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
public class AddressController {

    @Autowired private AddressService addressService;
    @Autowired private UserService userService;

    // 1. Xem danh sách địa chỉ
    @GetMapping
    public String viewAddresses(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userService.findByEmail(principal.getName());
        // Gọi hàm findByUser (đã định nghĩa trong Service)
        List<Address> addresses = addressService.findByUser(user);
        
        model.addAttribute("addresses", addresses);
        return "client/account/addresses";
    }

    // 2. Thêm địa chỉ mới
    @PostMapping("/add")
    public String addAddress(@ModelAttribute Address address, 
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            User user = userService.findByEmail(principal.getName());
            address.setUser(user);
            
            // Hàm save() trong Service đã bao gồm logic:
            // - Tự động map Enum AddressType
            // - Tự động xử lý địa chỉ mặc định
            addressService.save(address);
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/account/addresses";
    }

    // 3. Xóa địa chỉ
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, Principal principal) {
        // Sử dụng deleteAddress (để khớp với tên hàm trong Interface gộp)
        addressService.deleteAddress(id);
        return "redirect:/account/addresses";
    }
    
    // 4. Đặt làm mặc định
    @GetMapping("/set-default/{id}")
    public String setDefault(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        
        // LƯU Ý QUAN TRỌNG: 
        // Service yêu cầu (Long addressId, Long userId)
        // Nên ở đây phải truyền user.getId()
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
            
            // Gọi Service để xử lý (Truyền ID user và Object chứa dữ liệu mới)
            addressService.updateAddress(user.getId(), address);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi sẽ được Service ném ra (ví dụ: Không có quyền, Không tìm thấy...)
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/account/addresses";
    }
}