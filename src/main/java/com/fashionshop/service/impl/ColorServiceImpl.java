package com.fashionshop.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.model.Color;
import com.fashionshop.repository.ColorRepository;
import com.fashionshop.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

	@Autowired
	private ColorRepository colorRepository;

	@Override
	public List<Color> getAllColors() {
		return colorRepository.findAll();
	}

	@Override
	public Page<Color> getAllColors(Pageable pageable) {
		return colorRepository.findAll(pageable);
	}

	@Override
	public Page<Color> searchColors(String keyword, Pageable pageable) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return colorRepository.findAll(pageable);
		}
		return colorRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
	}

	@Override
	public Color getColorById(Long id) {
		return colorRepository.findById(id).orElse(null);
	}

	@Override
	public void saveColor(Color color) {
		colorRepository.save(color);
	}

	@Override
	public void deleteColor(Long id) {
		colorRepository.deleteById(id);
	}
}