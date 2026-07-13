package com.fashionshop.controller.client;
import com.fashionshop.service.WishlistService;
import com.fashionshop.service.RecentlyViewedService;
import com.fashionshop.model.Product;
import com.fashionshop.service.FaqService;
import org.springframework.data.domain.PageRequest;
import com.fashionshop.repository.ProductRepository;
import com.fashionshop.model.LoginHistory;
import org.springframework.data.domain.Page;
import com.fashionshop.service.LoginHistoryService;
import com.fashionshop.model.WishlistItem;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

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
    @Autowired private WishlistService wishlistService;

    @GetMapping("/wishlist")
    public String myWishlist(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        List<WishlistItem> wishlist = wishlistService.getUserWishlist(user.getId());
        model.addAttribute("wishlist", wishlist);

        return "client/account/wishlist";
    }

    @Autowired private RecentlyViewedService recentlyViewedService;
    @Autowired private LoginHistoryService loginHistoryService;
    @Autowired private FaqService faqService;

    @GetMapping("/faq")
    public String faqPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);
        }
        model.addAttribute("faqs", faqService.getActiveFaqs());
        return "client/account/faq";
    }

    @GetMapping("/support")
    public String supportPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);
        }
        return "client/account/support";
    }

    @PostMapping("/support/submit")
    public String submitSupport(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success", "YÃªu cáº§u há»— trá»£ cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c gá»­i thÃ nh cÃ´ng. ChÃºng tÃ´i sáº½ pháº£n há»“i trong thá»\ufffdi gian sá»›m nháº¥t.");
        return "redirect:/account/support";
    }

    @GetMapping("/recently-viewed")
    public String recentlyViewed(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        List<Product> recentProducts = recentlyViewedService.getRecentlyViewedProducts(user, 40);
        model.addAttribute("recentProducts", recentProducts);

        return "client/account/recently-viewed";
    }

    @GetMapping("/login-history")
    public String loginHistory(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        List<LoginHistory> loginHistories = loginHistoryService.getLoginHistoryByUser(user, 50);
        model.addAttribute("loginHistories", loginHistories);

        return "client/account/login-history";
    }

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
            redirectAttributes.addFlashAttribute("successMessage", "Cáº­p nháº­t thÃ´ng tin thÃ nh cÃ´ng!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lá»—i: " + e.getMessage());
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

        try {
            userService.changePassword(user, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Ä\ufffdá»•i máº­t kháº©u thÃ nh cÃ´ng!");
        } catch (FashionShopException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máº­t kháº©u hiá»‡n táº¡i khÃ´ng Ä‘Ãºng hoáº·c xÃ¡c nháº­n máº­t kháº©u khÃ´ng khá»›p!");
        }
        return "redirect:/account/profile";
    }

    @GetMapping("/orders")
    public String myOrders(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        Page<Order> orderPage = orderService.getOrdersByUser(user.getId(), PageRequest.of(page, 5));
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("page", orderPage);

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
            
            redirectAttributes.addFlashAttribute("successMessage", "Ä\ufffdÃ£ há»§y Ä‘Æ¡n hÃ ng thÃ nh cÃ´ng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lá»—i: " + e.getMessage());
        }
        
        return "redirect:/account/orders";
    }
    
 // 6. Xem chi tiáº¿t Ä‘Æ¡n hÃ ng
    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, 
                                  Model model, 
                                  Principal principal) {
        if (principal == null) return "redirect:/login";

        try {
            User user = userService.findByEmail(principal.getName());
            
            // Láº¥y Ä‘Æ¡n hÃ ng theo ID
            Order order = orderService.getOrderById(id);

            // Báº¢O Máº¬T: Kiá»ƒm tra xem Ä‘Æ¡n nÃ y cÃ³ Ä‘Ãºng cá»§a user Ä‘Ã³ khÃ´ng?
            // (TrÃ¡nh trÆ°á»\ufffdng há»£p Ã´ng A nháº­p ID Ä‘Æ¡n hÃ ng cá»§a Ã´ng B Ä‘á»ƒ xem trá»™m)
            if (!order.getUser().getId().equals(user.getId())) {
                return "redirect:/account/orders?error=access_denied";
            }

            model.addAttribute("order", order);
            model.addAttribute("user", user); // Ä\ufffdá»ƒ hiá»‡n sidebar

            return "client/account/order-detail"; // Tráº£ vá»\ufffd file HTML chi tiáº¿t

        } catch (Exception e) {
            return "redirect:/account/orders";
        }
    }
    
}