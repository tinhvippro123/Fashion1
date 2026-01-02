package com.fashionshop.controller.client;

import com.fashionshop.model.Order;
import com.fashionshop.model.User;
import com.fashionshop.service.OrderService;
import com.fashionshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account")
public class ClientAccountController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        
        model.addAttribute("user", user);
        model.addAttribute("displayLastName", user.getLastName());
        model.addAttribute("displayFirstName", user.getFirstName());

        return "client/account/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("email") String email,
                                @RequestParam(value = "gender", required = false) String gender,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            userService.updateProfile(user, email, gender);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/account/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword, 
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, 
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());

        if (!userService.checkPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
            return "redirect:/account/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "redirect:/account/profile";
        }

        userService.changePassword(user, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/account/profile";
    }

    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        List<Order> orders = orderService.getOrdersByUser(user.getId());
        model.addAttribute("orders", orders);

        return "client/account/orders";
    }
    
    @GetMapping("/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, 
                              Principal principal, 
                              RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        try {
            User user = userService.findByEmail(principal.getName());
            orderService.cancelOrder(id, user.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/account/orders";
    }
    
 // 6. Xem chi tiết đơn hàng
    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, 
                                  Model model, 
                                  Principal principal) {
        if (principal == null) return "redirect:/login";

        try {
            User user = userService.findByEmail(principal.getName());
            
            // Lấy đơn hàng theo ID
            Order order = orderService.getOrderById(id);

            // BẢO MẬT: Kiểm tra xem đơn này có đúng của user đó không?
            // (Tránh trường hợp ông A nhập ID đơn hàng của ông B để xem trộm)
            if (!order.getUser().getId().equals(user.getId())) {
                return "redirect:/account/orders?error=access_denied";
            }

            model.addAttribute("order", order);
            model.addAttribute("user", user); // Để hiện sidebar

            return "client/account/order-detail"; // Trả về file HTML chi tiết

        } catch (Exception e) {
            return "redirect:/account/orders";
        }
    }
    
}