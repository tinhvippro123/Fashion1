package com.fashionshop.controller.admin;

import com.fashionshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // 1. Lấy tổng doanh thu (Giả sử bạn có hàm tính tổng tiền các đơn đã thanh toán)
        Double revenue = orderService.calculateTotalRevenue(); 
        model.addAttribute("totalRevenue", revenue);

        // 2. Đếm số lượng đơn hàng mới (Trạng thái PENDING)
        long newOrders = orderService.countByStatus("PENDING");
        model.addAttribute("totalOrders", newOrders);

        // 3. Đếm tổng sản phẩm
        long totalProducts = productService.countAllProducts();
        model.addAttribute("totalProducts", totalProducts);

        // 4. Đếm tổng khách hàng
        long totalUsers = userService.countAllCustomers();
        model.addAttribute("totalUsers", totalUsers);

        return "admin/dashboard"; // Trả về file dashboard.html
    }
}