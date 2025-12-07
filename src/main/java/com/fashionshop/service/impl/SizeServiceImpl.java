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
        // Có thể thêm sort theo sortOrder nếu muốn: return sizeRepository.findAll(Sort.by("sortOrder"));
        return sizeRepository.findAll();
    }

    @Override
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElse(null);
    }

    @Override
    public void saveSize(Size size) {
        // Logic nghiệp vụ đơn giản: Chuẩn hóa tên (Viết hoa hết: s -> S)
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