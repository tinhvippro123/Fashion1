package com.fashionshop.controller.client;

import com.fashionshop.model.User;
import com.fashionshop.service.UserService;
import com.fashionshop.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    @Autowired
    private WishlistService wishlistService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleWishlist(@PathVariable Long productId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("status", "error");
            response.put("message", "Vui lÃ²ng Ä‘Äƒng nháº­p Ä‘á»ƒ sá»­ dá»¥ng tÃ­nh nÄƒng nÃ y!");
            return ResponseEntity.status(401).body(response);
        }

        try {
            User user = userService.findByEmail(principal.getName());
            boolean added = wishlistService.toggleWishlist(user.getId(), productId);
            
            response.put("status", "success");
            response.put("added", added);
            response.put("message", added ? "Ä\ufffdÃ£ thÃªm vÃ o danh sÃ¡ch yÃªu thÃ­ch!" : "Ä\ufffdÃ£ gá»¡ khá»\ufffdi danh sÃ¡ch yÃªu thÃ­ch!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Ä\ufffdÃ£ xáº£y ra lá»—i: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
