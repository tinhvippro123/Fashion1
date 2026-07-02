package com.fashionshop.controller.admin;

import com.fashionshop.model.Contact;
import com.fashionshop.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/contacts")
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public String listContacts(Model model) {
        model.addAttribute("contacts", contactService.findAll());
        return "admin/contact/list";
    }

    @GetMapping("/read/{id}")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Contact contact = contactService.findById(id);
            if (contact != null) {
                contact.setIsRead(true);
                contactService.save(contact);
                redirectAttributes.addFlashAttribute("success", "Đã đánh dấu là đã đọc!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/contacts";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa lời nhắn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa lời nhắn: " + e.getMessage());
        }
        return "redirect:/admin/contacts";
    }
}
