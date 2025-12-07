package com.fashionshop.controller.admin;

import com.fashionshop.model.Size;
import com.fashionshop.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sizes")
public class AdminSizeController {

    @Autowired
    private SizeService sizeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sizes", sizeService.getAllSizes());
        return "admin/size/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("size", new Size());
        return "admin/size/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("size", sizeService.getSizeById(id));
        return "admin/size/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("size") Size size) {
        sizeService.saveSize(size);
        return "redirect:/admin/sizes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return "redirect:/admin/sizes";
    }
}