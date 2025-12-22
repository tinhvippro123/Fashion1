package com.fashionshop.controller.client;

import com.fashionshop.model.Order;
import com.fashionshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order/tracking")
public class OrderTrackingController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String trackingPage(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "phone", required = false) String phone,
                               Model model) {
        
        // Nếu URL có tham số (từ trang Success bấm sang) -> Tự động tìm luôn
        if (id != null && phone != null && !phone.isEmpty()) {
            try {
                Order order = orderService.findOrderForTracking(id, phone);
                model.addAttribute("order", order);
                model.addAttribute("trackingId", id);
                model.addAttribute("trackingPhone", phone);
                return "client/tracking-result"; // Trang kết quả
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Không tìm thấy đơn hàng #" + id + " với số điện thoại này.");
            }
        }

        // Mặc định hiển thị form nhập
        return "client/tracking-form";
    }
}