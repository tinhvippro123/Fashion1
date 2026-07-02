package com.fashionshop.service.impl;

import com.fashionshop.model.Page;
import com.fashionshop.repository.PageRepository;
import com.fashionshop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageRepository pageRepository;

    @Override
    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    @Override
    public Page findById(Long id) {
        return pageRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Page> findBySlug(String slug) {
        return pageRepository.findBySlugAndIsActiveTrue(slug);
    }

    @Override
    public Page save(Page page) {
        return pageRepository.save(page);
    }

    @Override
    public void delete(Long id) {
        pageRepository.deleteById(id);
    }
}
