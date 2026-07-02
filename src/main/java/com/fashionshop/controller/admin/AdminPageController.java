package com.fashionshop.controller.admin;

import com.fashionshop.model.Page;
import com.fashionshop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pages")
public class AdminPageController {

    @Autowired
    private PageService pageService;

    @GetMapping
    public String listPages(Model model) {
        model.addAttribute("pages", pageService.findAll());
        return "admin/page/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("page", new Page());
        return "admin/page/form";
    }

    @PostMapping("/create")
    public String createPage(@ModelAttribute("page") Page page, RedirectAttributes redirectAttributes) {
        try {
            pageService.save(page);
            redirectAttributes.addFlashAttribute("success", "Thêm trang mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/pages";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Page page = pageService.findById(id);
        if (page == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy trang!");
            return "redirect:/admin/pages";
        }
        model.addAttribute("page", page);
        return "admin/page/form";
    }

    @PostMapping("/edit/{id}")
    public String updatePage(@PathVariable Long id, @ModelAttribute("page") Page pageDetails, RedirectAttributes redirectAttributes) {
        try {
            Page existingPage = pageService.findById(id);
            if (existingPage != null) {
                existingPage.setTitle(pageDetails.getTitle());
                existingPage.setSlug(pageDetails.getSlug());
                existingPage.setContent(pageDetails.getContent());
                existingPage.setIsActive(pageDetails.getIsActive());
                pageService.save(existingPage);
                redirectAttributes.addFlashAttribute("success", "Cập nhật trang thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/pages";
    }

    @GetMapping("/delete/{id}")
    public String deletePage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pageService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa trang thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa trang. Có thể trang đang được sử dụng.");
        }
        return "redirect:/admin/pages";
    }
}
