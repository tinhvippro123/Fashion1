package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Color;

public interface ColorService {
	List<Color> getAllColors();

	Color getColorById(Long id);

	void saveColor(Color color);

	void deleteColor(Long id);
}