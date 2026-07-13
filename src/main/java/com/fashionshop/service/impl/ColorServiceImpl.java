package com.fashionshop.service.impl;
import com.fashionshop.dto.admin.ColorRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
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
		return colorRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.COLOR_NOT_FOUND));
	}

	@Override
	public Color saveColor(Color color) {
		return colorRepository.save(color);
	}

	@Override
	public void deleteColor(Long id) {
		colorRepository.deleteById(id);
	}

    @Override
    public Color createColor(ColorRequestDTO request) {
        Color entity = new Color();
        entity.setName(request.getName());
        entity.setHexCode(request.getHexCode());
        return saveColor(entity);
    }

    @Override
    public Color updateColor(Long id, ColorRequestDTO request) {
        Color entity = getColorById(id);
        entity.setName(request.getName());
        entity.setHexCode(request.getHexCode());
        return saveColor(entity);
    }


}
