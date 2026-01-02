package com.fashionshop.controller.admin;

import com.fashionshop.model.Category;
import com.fashionshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/category/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("category", new Category());
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/category/form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("category", categoryService.getCategoryById(id));
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/category/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("category") Category category) {
		categoryService.saveCategory(category);
		return "redirect:/admin/categories";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return "redirect:/admin/categories";
	}
}