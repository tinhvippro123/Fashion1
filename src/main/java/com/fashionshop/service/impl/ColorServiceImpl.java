package com.fashionshop.service.impl;

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