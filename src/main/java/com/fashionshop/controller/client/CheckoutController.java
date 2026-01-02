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
    private AddressService addressService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal, HttpSession session) {
        Cart cart = resolveCart(principal, session);

        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/";
        }

        double totalAmount = cartService.calculateTotalPrice(cart);
        double shippingFee = 0;
        double finalTotal = totalAmount + shippingFee;

        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalTotal", finalTotal);

        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user);

            List<Address> addresses = addressService.findByUser(user);
            model.addAttribute("userAddresses", addresses);

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
            Order order = orderService.placeOrder(user, sessionId, receiverName, phone, province, district, ward, street, note, paymentMethod);

            return "redirect:/order/success/" + order.getId();

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    @GetMapping("/success/{orderId}")
    public String successPage(@PathVariable Long orderId, Model model, Principal principal) {
    	Order order = orderService.getOrderById(orderId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("phone", order.getPhone());
        model.addAttribute("isLoggedIn", principal != null);
        return "client/order-success";
    }

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