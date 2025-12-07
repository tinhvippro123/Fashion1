package com.fashionshop.controller.admin;

import com.fashionshop.model.Color;
import com.fashionshop.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/colors")
public class AdminColorController {

	@Autowired
	private ColorService colorService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("colors", colorService.getAllColors());
		return "admin/color/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("color", new Color());
		return "admin/color/form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("color", colorService.getColorById(id));
		return "admin/color/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("color") Color color) {
		colorService.saveColor(color);
		return "redirect:/admin/colors";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		colorService.deleteColor(id);
		return "redirect:/admin/colors";
	}
}