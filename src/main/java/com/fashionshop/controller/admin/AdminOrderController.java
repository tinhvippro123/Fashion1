package com.fashionshop.controller.admin;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Order;
import com.fashionshop.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping
	public String listOrders(Model model) {
		model.addAttribute("orders", orderService.getAllOrders());
		return "admin/order/list";
	}

	@GetMapping("/detail/{id}")
	public String orderDetail(@PathVariable Long id, Model model) {
		Order order = orderService.getOrderById(id);
		model.addAttribute("order", order);
		model.addAttribute("orderStatuses", OrderStatus.values());
		return "admin/order/detail";
	}

	@PostMapping("/update-status/{id}")
	public String updateStatus(@PathVariable Long id, @RequestParam("status") OrderStatus status) {
		orderService.updateOrderStatus(id, status);
		return "redirect:/admin/orders/detail/" + id;
	}

}