package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Color;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColorService {
	List<Color> getAllColors();
	Page<Color> getAllColors(Pageable pageable);
	Page<Color> searchColors(String keyword, Pageable pageable);

	Color getColorById(Long id);

	void saveColor(Color color);

	void deleteColor(Long id);
}