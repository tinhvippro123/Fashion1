package com.fashionshop.controller.client;
import com.fashionshop.service.BannerService;
import com.fashionshop.model.Banner;

import com.fashionshop.model.Contact;
import com.fashionshop.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;
    
    @Autowired
    private BannerService bannerService;

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contact", new Contact());
        
        java.util.List<Banner> contactBanners = bannerService.getActiveBannersByPosition("CONTACT");
        if (!contactBanners.isEmpty()) {
            model.addAttribute("contactBanner", contactBanners.get(0));
        }
        
        return "client/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@ModelAttribute("contact") Contact contact, RedirectAttributes redirectAttributes) {
        try {
            contactService.save(contact);
            redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi trong thời gian sớm nhất.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi, vui lòng thử lại sau.");
        }
        return "redirect:/contact";
    }
}
