package com.fashionshop.controller.api;



import com.fashionshop.dto.ApiResponse;

import com.fashionshop.exception.ErrorCode;

import com.fashionshop.exception.FashionShopException;

import com.fashionshop.model.Order;

import com.fashionshop.service.OrderService;

import com.fashionshop.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.web.bind.annotation.*;



import java.security.Principal;

import java.util.HashMap;

import java.util.List;

import java.util.Map;



@RestController("apiOrderController")

@RequestMapping("/api/v1/orders")

public class OrderController {



    @Autowired

    private OrderService orderService;



    @Autowired

    private UserService userService;



    @GetMapping

    public ApiResponse<Map<String, Object>> getOrders(

            @RequestParam(defaultValue = "0") int page,

            Principal principal) {

        return ApiResponse.success(orderService.getOrdersDataByUser(principal.getName(), page));

    }



    @GetMapping("/{id}")

    public ApiResponse<Order> getOrderDetail(

            @PathVariable Long id,

            Principal principal) {

        Order order = orderService.getOrderByIdAndEmail(id, principal.getName());

        return ApiResponse.success(order);

    }



    @PutMapping("/cancel/{id}")

    public ApiResponse<String> cancelOrder(

            @PathVariable Long id,

            Principal principal) {

        orderService.cancelOrderByEmail(id, principal.getName());

        return ApiResponse.success("Đã hủy đơn hàng thành công!");

    }

}

