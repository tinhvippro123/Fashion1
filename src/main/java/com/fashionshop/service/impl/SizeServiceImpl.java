package com.fashionshop.service.impl;
import com.fashionshop.dto.admin.SizeRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<Size> getAllSizes(Pageable pageable) {
        return sizeRepository.findAll(pageable);
    }

    @Override
    public Page<Size> searchSizes(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sizeRepository.findAll(pageable);
        }
        return sizeRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.SIZE_NOT_FOUND));
    }

    @Override
    public Size saveSize(Size size) {
        if (size.getName() != null) {
            size.setName(size.getName().toUpperCase());
        }
        return sizeRepository.save(size);
    }

    @Override
    public void deleteSize(Long id) {
        sizeRepository.deleteById(id);
    }

    @Override
    public Size createSize(SizeRequestDTO request) {
        Size entity = new Size();
        entity.setName(request.getName());
        return saveSize(entity);
    }

    @Override
    public Size updateSize(Long id, SizeRequestDTO request) {
        Size entity = getSizeById(id);
        entity.setName(request.getName());
        return saveSize(entity);
    }


}
