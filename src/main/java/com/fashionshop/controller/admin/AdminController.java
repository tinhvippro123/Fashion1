package com.fashionshop.controller.admin;

import com.fashionshop.service.*;

import java.util.List;

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

    @GetMapping(value = {"/", "/dashboard"})
    public String showDashboard(Model model) {
        Double revenue = orderService.calculateTotalRevenue(); 
        model.addAttribute("totalRevenue", revenue);

        long newOrders = orderService.countByStatus("PENDING");
        model.addAttribute("totalOrders", newOrders);

        long totalProducts = productService.countAllProducts();
        model.addAttribute("totalProducts", totalProducts);

        long totalUsers = userService.countAllCustomers();
        model.addAttribute("totalUsers", totalUsers);

     // --- PHẦN MỚI: DỮ LIỆU BIỂU ĐỒ ---
        
        // 1. Dữ liệu biểu đồ tròn (Pie Chart)
        List<Long> statusData = orderService.getOrderStatusCounts();
        model.addAttribute("statusData", statusData);

        // 2. Dữ liệu biểu đồ vùng (Area Chart)
        List<Double> revenueData = orderService.getRevenueLast7Days();
        model.addAttribute("revenueData", revenueData);
        
        return "admin/dashboard";
    }
}