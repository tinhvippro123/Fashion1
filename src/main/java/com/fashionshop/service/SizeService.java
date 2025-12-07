package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Size;

public interface SizeService {
	List<Size> getAllSizes();

	Size getSizeById(Long id);

	void saveSize(Size size);

	void deleteSize(Long id);
}