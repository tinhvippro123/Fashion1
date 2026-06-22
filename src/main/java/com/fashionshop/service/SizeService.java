package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Size;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SizeService {
	List<Size> getAllSizes();
	Page<Size> getAllSizes(Pageable pageable);
	Page<Size> searchSizes(String keyword, Pageable pageable);

	Size getSizeById(Long id);

	void saveSize(Size size);

	void deleteSize(Long id);
}