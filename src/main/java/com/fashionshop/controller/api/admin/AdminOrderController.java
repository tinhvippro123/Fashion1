package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.OrderUpdateStatusDTO;
import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Order;
import com.fashionshop.service.OrderService;
import com.fashionshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<Page<Order>> list(@RequestParam(defaultValue = "0") int page, 
                                         @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return ApiResponse.success(orderService.searchOrders(keyword, PageRequest.of(page, 10)));
        } else {
            return ApiResponse.success(orderService.getAllOrders(PageRequest.of(page, 10)));
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderUpdateStatusDTO request) {
        orderService.updateOrderStatus(id, OrderStatus.valueOf(request.getStatus()));
        return ApiResponse.success("Cập nhật trạng thái đơn hàng thành công");
    }

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalRevenue", orderService.calculateTotalRevenue());
        data.put("orderStatusCounts", orderService.getOrderStatusCounts());
        data.put("revenueLast7Days", orderService.getRevenueLast7Days());
        data.put("totalCustomers", userService.countAllCustomers());
        return ApiResponse.success(data);
    }
}
