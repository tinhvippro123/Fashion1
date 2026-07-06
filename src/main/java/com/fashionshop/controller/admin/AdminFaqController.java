package com.fashionshop.controller.admin;

import com.fashionshop.model.Faq;
import com.fashionshop.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/faqs")
public class AdminFaqController {

    @Autowired
    private FaqService faqService;

    @GetMapping
    public String listFaqs(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("faqs", faqService.searchFaqs(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("faqs", faqService.getAllFaqs());
        }
        return "admin/faq/list";
    }

    @GetMapping("/add")
    public String addFaqForm(Model model) {
        model.addAttribute("faq", new Faq());
        return "admin/faq/form";
    }

    @PostMapping("/save")
    public String saveFaq(@ModelAttribute("faq") Faq faq, RedirectAttributes redirectAttributes) {
        faqService.saveFaq(faq);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu Câu hỏi thường gặp thành công!");
        return "redirect:/admin/faqs";
    }

    @GetMapping("/edit/{id}")
    public String editFaqForm(@PathVariable("id") Long id, Model model) {
        Faq faq = faqService.getFaqById(id);
        if (faq == null) {
            return "redirect:/admin/faqs";
        }
        model.addAttribute("faq", faq);
        return "admin/faq/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteFaq(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        faqService.deleteFaq(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa Câu hỏi thường gặp thành công!");
        return "redirect:/admin/faqs";
    }
}
