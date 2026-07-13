package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.Order;
import com.fashionshop.model.User;
import com.fashionshop.service.OrderService;
import com.fashionshop.service.UserService;
import com.fashionshop.dto.CheckoutRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController("apiCheckoutController")
@RequestMapping("/api/v1/checkout")
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/place-order")
    public ApiResponse<Map<String, Object>> placeOrder(
            @Valid @RequestBody CheckoutRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId,
            Principal principal) {

        String email = principal != null ? principal.getName() : null;
        return ApiResponse.success(orderService.placeOrderData(email, sessionId, 
                request.getReceiverName(), request.getPhone(), 
                request.getProvince(), request.getDistrict(), 
                request.getWard(), request.getStreet(), 
                request.getNote(), request.getPaymentMethod()));
    }
}
