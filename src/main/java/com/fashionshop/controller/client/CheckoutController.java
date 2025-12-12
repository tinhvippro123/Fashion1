package com.fashionshop.controller.client;

import com.fashionshop.model.Address;
import com.fashionshop.model.Cart;
import com.fashionshop.model.Order;
import com.fashionshop.model.User;
import com.fashionshop.service.AddressService;
import com.fashionshop.service.CartService;
import com.fashionshop.service.OrderService;
import com.fashionshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService; // Inject thêm Service Địa chỉ

    // 1. Hiển thị trang Checkout (GET)
    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal, HttpSession session) {
        Cart cart = resolveCart(principal, session);

        // Nếu giỏ trống -> Đá về trang chủ
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/";
        }

        // Tính toán tiền
        double totalAmount = cartService.calculateTotalPrice(cart);
        // Lưu ý: Logic phí ship thực tế có thể phức tạp hơn
        double shippingFee = 0; // Để 0đ cho giống ảnh bạn gửi, hoặc tính toán tùy ý
        double finalTotal = totalAmount + shippingFee;

        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalTotal", finalTotal);

        // --- PHẦN XỬ LÝ ĐỊA CHỈ & LOGIN ---
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user);

            // Lấy danh sách địa chỉ của User để hiển thị Card
            List<Address> addresses = addressService.findByUser(user);
            model.addAttribute("userAddresses", addresses);

            // Tìm địa chỉ mặc định để hiển thị sẵn
            Address defaultAddr = addresses.stream()
                    .filter(Address::getIsDefault)
                    .findFirst()
                    .orElse(addresses.isEmpty() ? null : addresses.get(0));
            
            model.addAttribute("defaultAddress", defaultAddr);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "client/checkout";
    }

    // 2. Xử lý Đặt hàng (POST)
    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("phone") String phone,
            @RequestParam("province") String province,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("street") String street,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "paymentMethod", defaultValue = "COD") String paymentMethod,
            Principal principal, 
            HttpSession session, 
            RedirectAttributes redirectAttributes) {

        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        }

        String sessionId = (String) session.getAttribute("CART_SESSION_ID");

        try {
            // GỌI SERVICE ĐỂ CHỐT ĐƠN
            Order order = orderService.placeOrder(user, sessionId, receiverName, phone, province, district, ward, street, note, paymentMethod);

            // Thành công -> Chuyển sang trang Cảm ơn
            return "redirect:/order/success/" + order.getId();

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    // 3. Trang Thông báo Thành công (Giữ nguyên)
    @GetMapping("/success/{orderId}")
    public String successPage(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "client/order-success";
    }

    // --- Helper lấy giỏ hàng ---
    private Cart resolveCart(Principal principal, HttpSession session) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user != null) {
                return cartService.getCartByUser(user.getId());
            }
        }
        String sessionId = (String) session.getAttribute("CART_SESSION_ID");
        return (sessionId != null) ? cartService.getCartBySession(sessionId) : null;
    }
}