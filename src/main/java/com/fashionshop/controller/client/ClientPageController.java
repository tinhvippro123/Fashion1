package com.fashionshop.controller.client;

import com.fashionshop.model.Page;
import com.fashionshop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ClientPageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/pages/{slug}")
    public String showPage(@PathVariable String slug, Model model) {
        Optional<Page> pageOpt = pageService.findBySlug(slug);
        
        if (pageOpt.isPresent()) {
            model.addAttribute("page", pageOpt.get());
            return "client/page";
        } else {
            return "error/404";
        }
    }
}
