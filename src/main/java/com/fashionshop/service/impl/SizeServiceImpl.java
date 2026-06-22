package com.fashionshop.service.impl;

import com.fashionshop.model.Size;
import com.fashionshop.repository.SizeRepository;
import com.fashionshop.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    @Override
    public org.springframework.data.domain.Page<Size> getAllSizes(org.springframework.data.domain.Pageable pageable) {
        return sizeRepository.findAll(pageable);
    }

    @Override
    public org.springframework.data.domain.Page<Size> searchSizes(String keyword, org.springframework.data.domain.Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sizeRepository.findAll(pageable);
        }
        return sizeRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElse(null);
    }

    @Override
    public void saveSize(Size size) {
        if (size.getName() != null) {
            size.setName(size.getName().toUpperCase());
        }
        sizeRepository.save(size);
    }

    @Override
    public void deleteSize(Long id) {
        sizeRepository.deleteById(id);
    }
}