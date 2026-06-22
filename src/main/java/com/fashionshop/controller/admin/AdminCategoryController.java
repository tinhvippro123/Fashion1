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
	public String list(@RequestParam(defaultValue = "0") int page, 
	                   @RequestParam(value = "keyword", required = false) String keyword, 
	                   Model model) {
		org.springframework.data.domain.Page<Category> categoryPage;
		if (keyword != null && !keyword.isEmpty()) {
			categoryPage = categoryService.searchCategories(keyword, org.springframework.data.domain.PageRequest.of(page, 10));
			model.addAttribute("keyword", keyword);
		} else {
			categoryPage = categoryService.getAllCategories(org.springframework.data.domain.PageRequest.of(page, 10));
		}
		model.addAttribute("categories", categoryPage.getContent());
		model.addAttribute("page", categoryPage);
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